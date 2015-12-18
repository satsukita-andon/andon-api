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

  def opt(car: SyntaxProvider[ClassArticleRevision])(rs: WrappedResultSet): Option[ClassArticleRevision] =
    rs.intOpt(car.resultName.id).map(_ => ClassArticleRevision(car)(rs))

  def findAll(
    times: OrdInt, grade: Short, `class`: Short, paging: Paging
  )(implicit s: DBSession): Seq[(ClassArticle, ClassArticleRevision)] = {
    ClassModel.findId(times, grade, `class`).map { classId =>
      withSQL {
        select.from(ClassArticle as ca)
          .leftJoin(ClassArticleRevision as car)
          .on(SQLSyntax.eq(ca.id, car.articleId).and
            .eq(ca.latestRevisionNumber, car.revisionNumber))
          .where
          .eq(ca.classId, classId)
      }.one(ClassArticle(ca))
        .toOptionalOne(opt(car))
        .map { (article, revision) => (article, revision) } // TODO: option
        .list
        .apply()
    }.getOrElse(Seq())
  }

  def count(classId: Short)(implicit s: DBSession): Long = {
    ClassArticle.countBy(SQLSyntax.eq(ca.classId, classId))
  }
  def count(times: OrdInt, grade: Short, `class`: Short)(implicit s: DBSession): Long = {
    ClassModel.findId(times, grade, `class`).map(count).getOrElse(0L)
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
