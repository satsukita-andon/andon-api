package andon.api.models

import scalikejdbc._

import andon.api.models.generated.Class
import andon.api.util.OrdInt

object ClassModel {
  val c = Class.c
  def findId(times: OrdInt, grade: Short, `class`: Short)(implicit s: DBSession): Option[Short] = {
    withSQL {
      select(c.id).from(Class as c).where
        .eq(c.times, times.raw).and
        .eq(c.grade, grade).and
        .eq(c.`class`, `class`)
    }.map(_.short(c.resultName.id)).single.apply()
  }
}
