package andon.api.models

import cats.data.Xor
import org.joda.time.DateTime
import scalikejdbc._

import andon.api.errors._
import andon.api.models.generated.{ ClassArticle, ClassArticleRevision }
import andon.api.util._

object ClassArticleModel {
  def create(
    userId: Int,
    times: OrdInt,
    grade: Short,
    `class`: Short,
    status: PublishingStatus,
    title: String,
    body: String,
    comment: String
  )(implicit s: DBSession): Xor[AndonError, (ClassArticle, ClassArticleRevision)] = {
    ClassModel.findId(times, grade, grade).map { classId =>
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
    }.getOrElse(Xor.left(ResourceNotFound(s"${times}${grade}-${`class`} is not found.")))
  }
}
