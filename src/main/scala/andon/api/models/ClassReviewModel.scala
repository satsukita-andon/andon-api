package andon.api.models

import org.joda.time.DateTime
import scalikejdbc._

import andon.api.models.generated._
import andon.api.util._

object ClassReviewModel extends ClassReviewModel {
}
trait ClassReviewModel {

  val cr = ClassReview.cr
  val u = User.u

  def findAll(classId: Short, paging: Paging)(implicit s: DBSession): Seq[(ClassReview, User)] = {
    withSQL {
      paging.sql {
        select.from(ClassReview as cr)
          .innerJoin(User as u).on(u.id, cr.userId)
          .where
          .eq(cr.classId, classId)
      }
    }.one(ClassReview(cr))
      .toOne(User(u))
      .map((cr, u) => (cr, u))
      .list.apply()
  }

  def count(classId: Short)(implicit s: DBSession): Long = {
    ClassReview.countBy(sqls.eq(cr.classId, classId))
  }
}
