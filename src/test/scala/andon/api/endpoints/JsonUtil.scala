package andon.api.endpoints

import io.circe._

trait JsonUtil {
  import scala.language.postfixOps

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
    def map[A](f: (Json) => A): Seq[A] = (for {
      c <- cursor.downArray
      jsons <- c.rights
    } yield jsons.map(f))
      .getOrElse(throw TraceException(s"not array:\n${cursor.focus}"))
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