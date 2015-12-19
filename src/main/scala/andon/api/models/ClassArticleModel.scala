package andon.api.models

import cats.data.Xor
import org.joda.time.DateTime
import scalikejdbc._

import andon.api.errors._
import andon.api.models.generated.{ ClassArticle, ClassArticleRevision }
import andon.api.util._

object ClassArticleModel {

  val ca = ClassArticle.ca
  val car = ClassArticleRevision.car

  def findAll(classId: Short, paging: Paging)(implicit s: DBSession): Seq[(ClassArticle, ClassArticleRevision)] = {
    withSQL {
      paging.sql {
        select.from(ClassArticle as ca)
          .innerJoin(ClassArticleRevision as car)
          .on(SQLSyntax.eq(ca.id, car.articleId).and
            .eq(ca.latestRevisionNumber, car.revisionNumber))
          .where
          .eq(ca.classId, classId)
          .orderBy(ca.id)
      }
    }.one(ClassArticle(ca))
      .toOne(ClassArticleRevision(car))
      .map { (article, revision) => (article, revision) }
      .list
      .apply()
  }
  def findAll(classId: ClassId, paging: Paging)(implicit s: DBSession): Seq[(ClassArticle, ClassArticleRevision)] = {
    ClassModel.findId(classId).map(findAll(_, paging)).getOrElse(Seq())
  }

  def count(classId: Short)(implicit s: DBSession): Long = {
    ClassArticle.countBy(SQLSyntax.eq(ca.classId, classId))
  }
  def count(classId: ClassId)(implicit s: DBSession): Long = {
    ClassModel.findId(classId).map(count).getOrElse(0L)
  }

  def create(
    userId: Int, // must be existing user id
    classId: Short, // must be existing class id
    status: PublishingStatus,
    title: String,
    body: String,
    comment: String
  )(implicit s: DBSession): Xor[AndonError, (ClassArticle, ClassArticleRevision)] = try {
    val now = DateTime.now
    val ca = ClassArticle.create(
      classId = classId,
      latestRevisionNumber = 1,
      status = status.code,
      createdBy = Some(userId),
      updatedBy = Some(userId),
      createdAt = now,
      updatedAt = now
    )
    val rev = ClassArticleRevision.create(
      articleId = ca.id,
      revisionNumber = 1,
      userId = Some(userId),
      title = title,
      body = body,
      comment = comment,
      createdAt = now
    )
    Xor.right((ca, rev))
  } catch {
    case e: java.sql.SQLException => Xor.left(Incorrect(e.getMessage)) // TODO: check exception type
  }
}
