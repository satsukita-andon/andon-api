package andon.api.jsons

import org.joda.time.DateTime

import andon.api.models.generated.{
  Class => ClassRow,
  Prize => PrizeRow,
  ClassArticle => ClassArticleRow,
  ClassArticleRevision => ClassArticleRevisionRow,
  User => UserRow
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
  comment: String,
  created_by: Option[Int],
  updated_by: Option[Int],
  created_at: DateTime,
  updated_at: DateTime
)

object ClassArticle {
  def apply(article: ClassArticleRow, revision: ClassArticleRevisionRow): ClassArticle = ClassArticle(
    id = article.id,
    revision_number = revision.revisionNumber,
    status = PublishingStatus.from(article.status).get, // TODO
    title = revision.title,
    comment = revision.comment,
    created_by = article.createdBy,
    updated_by = revision.userId,
    created_at = article.createdAt,
    updated_at = revision.createdAt
  )
}

final case class DetailedClassArticle(
  id: Int,
  `class`: Class,
  revision_number: Short,
  status: PublishingStatus,
  title: String,
  body: String,
  comment: String,
  created_by: Option[User],
  updated_by: Option[User],
  created_at: DateTime,
  updated_at: DateTime
)

object DetailedClassArticle {
  def apply(
    `class`: ClassRow,
    prizes: Seq[PrizeRow],
    article: ClassArticleRow,
    revision: ClassArticleRevisionRow,
    createdBy: Option[UserRow],
    updatedBy: Option[UserRow]
  ): DetailedClassArticle = DetailedClassArticle(
    id = article.id,
    `class` = Class(`class`, prizes),
    revision_number = revision.revisionNumber,
    status = PublishingStatus.from(article.status).get, // TODO
    title = revision.title,
    body = revision.body,
    comment = revision.comment,
    created_by = createdBy.map(User.apply),
    updated_by = updatedBy.map(User.apply),
    created_at = article.createdAt,
    updated_at = revision.createdAt
  )
}
