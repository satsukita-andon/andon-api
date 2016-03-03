package andon.api.models

import scalikejdbc._

import andon.api.models.generated.{ ClassImage, User }
import andon.api.util.Paging

object ClassImageModel extends ClassImageModel
trait ClassImageModel {

  private val u = User.u
  val ci = ClassImage.ci

  def findAll(classId: Short, paging: Paging)(implicit s: DBSession): Seq[(ClassImage, User)] = {
    withSQL {
      paging.sql {
        select.from(ClassImage as ci)
          .innerJoin(User as u).on(ci.userId, u.id)
          .where.eq(ci.classId, classId)
      }
    }.one(ClassImage(ci))
      .toOne(User(u))
      .map { (image, user) => (image, user) }
      .list
      .apply()
  }

  def random(num: Int)(implicit s: DBSession): Seq[(ClassImage, User)] = {
    withSQL {
      select.from(ClassImage as ci)
        .innerJoin(User as u).on(ci.userId, u.id)
        .orderBy(sqls"random()")
        .limit(num)
    }.one(ClassImage(ci))
      .toOne(User(u))
      .map { (image, user) => (image, user) }
      .list.apply()
  }

  def count(classId: Short)(implicit s: DBSession): Long = {
    ClassImage.countBy(SQLSyntax.eq(ci.classId, classId))
  }
}
