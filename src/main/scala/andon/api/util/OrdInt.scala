package andon.api.util

import scala.util.Try

case class OrdInt(raw: Int) {
  override def toString: String = {
    raw % 10 match {
      case 1 => raw + "st"
      case 2 => raw + "nd"
      case 3 => raw + "rd"
      case _ => raw + "th"
    }
  }
}

object OrdInt {
  def fromString(str: String): Option[OrdInt] = {
    val r = """(\d+)(st|nd|rd|th)""".r
    str match {
      case r(nstr, ord) => {
        Try(nstr.toInt).toOption.flatMap { n =>
          n % 10 match {
            case 1 if ord == "st" => Some(OrdInt(n))
            case 2 if ord == "nd" => Some(OrdInt(n))
            case 3 if ord == "rd" => Some(OrdInt(n))
            case p if p != 1 && p != 2 && p != 3 && ord == "th" => Some(OrdInt(n))
            case _ => None
          }
        }
      }
      case _ => None
    }
  }
}
