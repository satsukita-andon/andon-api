package andon.api.models

import scalikejdbc._

import andon.api.models.generated.{ Class, Prize }
import andon.api.util.OrdInt

object ClassModel {

  val c = Class.c
  val p = Prize.p

  def findId(times: OrdInt, grade: Short, `class`: Short)(implicit s: DBSession): Option[Short] = {
    withSQL {
      select(c.id).from(Class as c).where
        .eq(c.times, times.raw).and
        .eq(c.grade, grade).and
        .eq(c.`class`, `class`)
    }.map(_.short(c.resultName.id)).single.apply()
  }

  def findWithPrizes(times: OrdInt, grade: Short, `class`: Short)(implicit s: DBSession): Option[(Class, Seq[Prize])] = {
    // TODO: join
    Class.findBy(
      SQLSyntax.eq(c.times, times.raw).and
        .eq(c.grade, grade).and
        .eq(c.`class`, `class`)
    ).map { clazz =>
      val prizes = Prize.findAllBy(SQLSyntax.eq(p.id, clazz.id))
      (clazz, prizes)
    }
  }
}
