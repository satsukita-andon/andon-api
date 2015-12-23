package andon.api.jsons

import org.joda.time.DateTime

import andon.api.models.generated.{
  Article => ArticleRow,
  ArticleRevision => ArticleRevisionRow,
  User => UserRow
}
import andon.api.util._

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
