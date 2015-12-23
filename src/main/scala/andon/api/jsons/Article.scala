package andon.api.jsons

import andon.api.errors.{Validation, InvalidItem}
import cats.data.ValidatedNel
import org.joda.time.DateTime

import andon.api.models.generated.{
  Article => ArticleRow,
  ArticleRevision => ArticleRevisionRow,
  User => UserRow
}
import andon.api.util._

final case class ArticleCreation(
  status: PublishingStatus,
  editorial_right: EditorialRight,
  editors: Seq[Int],
  title: String,
  body: String,
  comment: String
) {
  val validate: ValidatedNel[InvalidItem, ArticleCreation] = {
    Validation.run(this, Seq(
      (title.length > 200) -> InvalidItem(
        field = "title",
        reason = "`title` must be less than or equal to 200 characters"
      ),
      (status == PublishingStatus.Suspended) -> InvalidItem(
        field = "status",
        reason = "`status` must not be suspended"
      )
    ))
  }
}

final case class Article(
  id: Int,
  revision_number: Short,
  status: PublishingStatus,
  title: String,
  comment: String,
  owner: User,
  editor: Option[User],
  created_at: DateTime,
  updated_at: DateTime
)

object Article {
  def apply(
    article: ArticleRow,
    owner: UserRow,
    revision: ArticleRevisionRow,
    editor: Option[UserRow]
  ): Article = Article(
    id = article.id,
    revision_number = revision.revisionNumber,
    status = PublishingStatus.from(article.status).get,
    title = revision.title,
    comment = revision.comment,
    owner = User(owner),
    editor = editor.map(User.apply),
    created_at = article.createdAt,
    updated_at = revision.createdAt
  )
}

final case class DetailedArticle(
  id: Int,
  revision_number: Short,
  status: PublishingStatus,
  title: String,
  body: String,
  comment: String,
  owner: User,
  editor: Option[User],
  created_at: DateTime,
  updated_at: DateTime
)

object DetailedArticle {
  def apply(
    article: ArticleRow,
    owner: UserRow,
    revision: ArticleRevisionRow,
    editor: Option[UserRow]
      // tags: Seq[String],
      // editors: Seq[UserRow]
  ): DetailedArticle = DetailedArticle(
    id = article.id,
    revision_number = revision.revisionNumber,
    status = PublishingStatus.from(article.status).get,
    title = revision.title,
    body = revision.body,
    comment = revision.comment,
    owner = User(owner),
    editor = editor.map(User.apply),
    created_at = article.createdAt,
    updated_at = revision.createdAt
  )
}
