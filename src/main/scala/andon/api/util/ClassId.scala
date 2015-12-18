package andon.api.util

import scala.util.Try

final case class ClassId(
  times: OrdInt,
  grade: Short,
  `class`: Short
) {
  override def toString = s"${times}${grade}-${`class`}"
}

object ClassId {
  def parse(str: String): Option[ClassId] = {
    val r = """(\d+(st|nd|rd|th))(\d)-(-?\d\d?)""".r
    str match {
      case r(t, _, g, c) => for {
        times <- OrdInt.parse(t)
        grade <- Try(g.toShort).toOption
        clazz <- Try(c.toShort).toOption
      } yield ClassId(times, grade, clazz)
      case _ => None
    }
  }
}
