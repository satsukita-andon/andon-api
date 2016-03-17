package andon.api.jsons

import cats.data.ValidatedNel
import org.joda.time.DateTime

import andon.api.errors._
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
) {
  val validate: ValidatedNel[InvalidItem, ClassArticleCreation] = {
    Validation.run(this, Seq(
      (title.length > 200) -> InvalidItem(
        field = "title",
        reason = "`title` must be less than or equal to 200 characters"
      )
    ))
  }
}

final case class ClassArticle(
  id: Int,
  revision_number: Short,
  status: PublishingStatus,
  title: String,
  comment: String,
  created_by: Option[User],
  updated_by: Option[User],
  created_at: DateTime,
  updated_at: DateTime
)

object ClassArticle {
  def apply(
    article: ClassArticleRow,
    revision: ClassArticleRevisionRow,
    createdBy: Option[UserRow],
    updatedBy: Option[UserRow]
  ): ClassArticle = ClassArticle(
    id = article.id,
    revision_number = revision.revisionNumber,
    status = PublishingStatus.from(article.status).get, // TODO
    title = revision.title,
    comment = revision.comment,
    created_by = createdBy.map(User.apply),
    updated_by = updatedBy.map(User.apply),
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
    tags: Seq[String],
    article: ClassArticleRow,
    revision: ClassArticleRevisionRow,
    createdBy: Option[UserRow],
    updatedBy: Option[UserRow]
  ): DetailedClassArticle = DetailedClassArticle(
    id = article.id,
    `class` = Class(`class`, prizes, tags),
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
