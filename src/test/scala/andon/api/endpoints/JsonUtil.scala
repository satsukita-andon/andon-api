package andon.api.endpoints

import io.circe._

trait JsonUtil {

  case class TraceException(msg: String) extends Exception(msg)

  implicit class ToTCursor(json: Json) {
    def t: TCursor = new TCursor(json.cursor)
  }

  // Cursor for Test cases
  class TCursor(val cursor: Cursor) {
    def ?(p: (Json) => Boolean): TCursor = cursor.downAt(p) match {
      case None => throw TraceException(s"could not match any elements in array:\n${cursor.focus}")
      case Some(c) => new TCursor(c)
    }
    def /(f: String): TCursor = cursor.downField(f) match {
      case None => throw TraceException(s"could not find field `${f}`:\n${cursor.focus}")
      case Some(c) => new TCursor(c)
    }
    override def equals(any: Any): Boolean =
      throw new UnsupportedOperationException("Unsupported types for (==) operator of TCursor. Please check the type of the right hand side of (==) operator.")
    def ==(n: Int): Boolean = asInt == n
    def ==(s: String): Boolean = asString == s
    def first: TCursor = cursor.downArray match {
      case None => throw TraceException(s"this is not array:\n${cursor.focus}")
      case Some(c) => new TCursor(c)
    }
    def map[A](f: (Json) => A): Seq[A] = {
      val fc = first.cursor
      fc.rights match {
        case None => throw TraceException(s"this is not an element of array :\n${cursor.focus}")
        case Some(jsons) => (fc.focus :: jsons).map(f)
      }
    }
    def focus: Json = cursor.focus
    def asInt: Int = asLong.toInt
    def asLong: Long = asNumber.toLong match {
      case None => throw TraceException(s"not a long number: ${cursor.focus}")
      case Some(long) => long
    }
    def asString: String = focus.asString match {
      case None => throw TraceException(s"not a string: ${cursor.focus}")
      case Some(str) => str
    }
    def asNumber: JsonNumber = focus.asNumber match {
      case None => throw TraceException(s"not a number: ${cursor.focus}")
      case Some(num) => num
    }
  }
}
