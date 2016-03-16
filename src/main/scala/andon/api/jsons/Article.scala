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
  def validate: ValidatedNel[InvalidItem, ArticleCreation] = {
    Validation.run(
      this, Seq(
        (title.length > 200) -> InvalidItem(
          field = "title",
          reason = "`title` must be less than or equal to 200 characters"
        ),
        (status == PublishingStatus.Suspended) -> InvalidItem(
          field = "status",
          reason = "`status` must not be suspended"
        )
      )
    )
  }
}

// for articles
final case class ArticleMetaModification(
  status: PublishingStatus,
  editorial_right: EditorialRight,
  editor_ids: Seq[Int]
) {
  def validate(
    current: ArticleMetaModification,
    isAdmin: Boolean,
    isOwner: Boolean
  ): ValidatedNel[InvalidItem, ArticleMetaModification] = {
    Validation.run(
      this, Seq(
        // not suspended -> suspended allowed only admin
        (!isAdmin && current.status != PublishingStatus.Suspended && status == PublishingStatus.Suspended) -> InvalidItem(
          field = "status",
          reason = "only admin can modify `status` to `suspended`"
        ),
        // suspended -> not suspended allowed only admin
        (!isAdmin && current.status == PublishingStatus.Suspended && status != PublishingStatus.Suspended) -> InvalidItem(
          field = "status",
          reason = "only admin can give back `status` from `suspended`"
        ),
        // modification of status allowed only admin and owner
        (!isAdmin && !isOwner && status != current.status) -> InvalidItem(
          field = "status",
          reason = "only admin and owner can modify `status`"
        ),
        // modification of editorial_right allowed only admin and owner
        (!isAdmin && !isOwner && editorial_right != current.editorial_right) -> InvalidItem(
          field = "editorial_right",
          reason = "only admin and owner can modify `editorial_right`"
        ),
        // modification of editorial_right allowed only admin and owner
        (!isAdmin && !isOwner && editor_ids.toSet != current.editor_ids.toSet) -> InvalidItem(
          field = "editor_ids",
          reason = "only admin and owner can modify `editor_ids`"
        )
      )
    )
  }
}
// for article_revisions
final case class ArticleContentModification(
  title: String,
  body: String,
  comment: String
) {
  def validate: ValidatedNel[InvalidItem, ArticleContentModification] = {
    Validation.run(
      this, Seq(
        // title
        (title.length > 200) -> InvalidItem(
          field = "title",
          reason = "`title` must be less than or equal to 200 characters"
        )
      )
    )
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
  tags: Seq[String],
  created_at: DateTime,
  updated_at: DateTime
)

object Article {
  def apply(
    article: ArticleRow,
    owner: UserRow,
    tags: Seq[String],
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
    tags = tags,
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
  tags: Seq[String],
  created_at: DateTime,
  updated_at: DateTime
)

object DetailedArticle {
  def apply(
    article: ArticleRow,
    owner: UserRow,
    tags: Seq[String],
    revision: ArticleRevisionRow,
    editor: Option[UserRow]
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
    tags = tags,
    created_at = article.createdAt,
    updated_at = revision.createdAt
  )
}
