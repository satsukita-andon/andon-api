package andon.api.models

import cats.data.Xor
import org.joda.time.DateTime
import scalikejdbc._

import andon.api.errors._
import andon.api.models.generated.{ ClassArticle, ClassArticleRevision }
import andon.api.util._

object ClassArticleModel {
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
