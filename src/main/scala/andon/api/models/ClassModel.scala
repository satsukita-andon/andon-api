package andon.api.models

import scalikejdbc._

import andon.api.models.generated.{ Class, Prize }
import andon.api.util.ClassId

object ClassModel {

  val c = Class.c
  val p = Prize.p

  def findId(classId: ClassId)(implicit s: DBSession): Option[Short] = {
    withSQL {
      select(c.result.id).from(Class as c).where
        .eq(c.times, classId.times.raw).and
        .eq(c.grade, classId.grade).and
        .eq(c.`class`, classId.`class`)
    }.map(_.short(c.resultName.id)).single.apply()
  }

  def findWithPrizes(classId: ClassId)(implicit s: DBSession): Option[(Class, Seq[Prize])] = {
    // TODO: join
    Class.findBy(
      SQLSyntax.eq(c.times, classId.times.raw).and
        .eq(c.grade, classId.grade).and
        .eq(c.`class`, classId.`class`)
    ).map { clazz =>
      val prizes = Prize.findAllBy(SQLSyntax.eq(p.id, clazz.id))
      (clazz, prizes)
    }
  }
}
