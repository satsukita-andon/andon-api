package andon.api.endpoints

import org.scalatest._
import io.circe._

trait JsonUtil { this: Assertions =>

  implicit class ToTCursor(json: Json) {
    def t: TCursor = new TCursor(json.cursor)
  }

  // Cursor for Testing
  class TCursor(val cursor: Cursor) {
    // find an element that satisfies predicate `p`
    // if not found, test case fails
    def ?(p: (Json) => Boolean): TCursor = cursor.downAt(p) match {
      case None => fail(s"could not match any elements in array:\n${cursor.focus}")
      case Some(c) => new TCursor(c)
    }
    // down field
    // if not found field name, test case fails
    def /(f: String): TCursor = cursor.downField(f) match {
      case None => fail(s"could not find field `${f}`:\n${cursor.focus}")
      case Some(c) => new TCursor(c)
    }
    // to prepend using predefined == operator
    override def equals(any: Any): Boolean =
      fail("Unsupported types for (==) operator of TCursor. Please check the type of the right hand side of (==) operator.")
    // this element has Int type and equals given value
    def ==(n: Int): Boolean = asInt == n
    // this element has String type and equals given value
    def ==(s: String): Boolean = asString == s
    // first element of this array
    // if this is not an array, test case fails
    def first: TCursor = cursor.downArray match {
      case None => fail(s"this is not array:\n${cursor.focus}")
      case Some(c) => new TCursor(c)
    }
    // map array
    // if this is not an array, test case fails
    def map[A](f: (Json) => A): Seq[A] = {
      val fc = first.cursor
      fc.rights match {
        case None => fail(s"this is not an element of array :\n${cursor.focus}") // if this is called, there is an implementation bug
        case Some(jsons) => (fc.focus :: jsons).map(f)
      }
    }
    // returns json object of this cursor
    def focus: Json = cursor.focus
    // cast json object to int
    def asInt: Int = asLong.toInt
    // cast json object to long
    def asLong: Long = asNumber.toLong match {
      case None => fail(s"not a long number: ${cursor.focus}")
      case Some(long) => long
    }
    // cast json object to string
    def asString: String = focus.asString match {
      case None => fail(s"not a string: ${cursor.focus}")
      case Some(str) => str
    }
    // cast json object to number representation of json
    def asNumber: JsonNumber = focus.asNumber match {
      case None => fail(s"not a number: ${cursor.focus}")
      case Some(num) => num
    }
  }
}
