package andon.api.jsons

import cats.data.ValidatedNel
import org.joda.time.DateTime

import andon.api.errors._
import andon.api.models.generated.{
  Class => ClassRow,
  Prize => PrizeRow,
  ClassResource => ClassResourceRow,
  ClassResourceRevision => ClassResourceRevisionRow,
  User => UserRow
}
import andon.api.util._

final case class ClassResourceCreation(
  status: PublishingStatus,
  title: String,
  description: String,
  url: String,
  comment: String
) {
  val validate: ValidatedNel[InvalidItem, ClassResourceCreation] = {
    Validation.run(this, Seq(
      (title.length > 200) -> InvalidItem(
        field = "title",
        reason = "`title` must be less than or equal to 200 characters"
      )
    ))
  }
}

final case class ClassResource(
  id: Int,
  revision_number: Short,
  status: PublishingStatus,
  title: String,
  description: String,
  url: String,
  comment: String,
  created_by: Option[User],
  updated_by: Option[User],
  created_at: DateTime,
  updated_at: DateTime
)

object ClassResource {
  def apply(
    resource: ClassResourceRow,
    revision: ClassResourceRevisionRow,
    createdBy: Option[UserRow],
    updatedBy: Option[UserRow]
  ): ClassResource = ClassResource(
    id = resource.id,
    revision_number = revision.revisionNumber,
    status = PublishingStatus.from(resource.status).get, // TODO
    title = revision.title,
    description = revision.description,
    url = revision.url,
    comment = revision.comment,
    created_by = createdBy.map(User.apply),
    updated_by = updatedBy.map(User.apply),
    created_at = resource.createdAt,
    updated_at = revision.createdAt
  )
}

final case class DetailedClassResource(
  id: Int,
  `class`: Class,
  revision_number: Short,
  status: PublishingStatus,
  title: String,
  description: String,
  url: String,
  comment: String,
  created_by: Option[User],
  updated_by: Option[User],
  created_at: DateTime,
  updated_at: DateTime
)

object DetailedClassResource {
  def apply(
    `class`: ClassRow,
    prizes: Seq[PrizeRow],
    tags: Seq[String],
    resource: ClassResourceRow,
    revision: ClassResourceRevisionRow,
    createdBy: Option[UserRow],
    updatedBy: Option[UserRow]
  ): DetailedClassResource = DetailedClassResource(
    id = resource.id,
    `class` = Class(`class`, prizes, tags),
    revision_number = revision.revisionNumber,
    status = PublishingStatus.from(resource.status).get, // TODO
    title = revision.title,
    description = revision.description,
    url = revision.url,
    comment = revision.comment,
    created_by = createdBy.map(User.apply),
    updated_by = updatedBy.map(User.apply),
    created_at = resource.createdAt,
    updated_at = revision.createdAt
  )
}
