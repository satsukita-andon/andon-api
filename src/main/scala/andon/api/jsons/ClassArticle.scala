package andon.api.jsons

import org.joda.time.DateTime

import andon.api.models.generated.{
  ClassArticle => ClassArticleRow,
  ClassArticleRevision => ClassArticleRevisionRow
}
import andon.api.util._

final case class ClassArticleCreation(
  status: PublishingStatus,
  title: String,
  body: String,
  comment: String
)

final case class ClassArticle(
  id: Int,
  revision_number: Short,
  status: PublishingStatus,
  title: String,
  body: String,
  comment: String,
  created_by: Option[Int],
  updated_by: Option[Int],
  created_at: DateTime,
  updated_at: DateTime
)

object ClassArticle {
  def apply(ca: ClassArticleRow, rev: ClassArticleRevisionRow): ClassArticle = {
    ClassArticle(
      id = ca.id,
      revision_number = rev.revisionNumber,
      status = PublishingStatus.from(ca.status).get, // TODO
      title = rev.title,
      body = rev.title,
      comment = rev.comment,
      created_by = ca.createdBy,
      updated_by = rev.userId,
      created_at = ca.createdAt,
      updated_at = rev.createdAt
    )
  }
}
