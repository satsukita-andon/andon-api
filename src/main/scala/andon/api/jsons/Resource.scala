package andon.api.jsons

import andon.api.errors.{Validation, InvalidItem}
import cats.data.ValidatedNel
import org.joda.time.DateTime

import andon.api.models.generated.{
  Resource => ResourceRow,
  ResourceRevision => ResourceRevisionRow,
  User => UserRow
}
import andon.api.util._

final case class ResourceCreation(
  status: PublishingStatus,
  editorial_right: EditorialRight,
  editors: Seq[Int],
  title: String,
  description: String,
  url: String,
  comment: String
) {
  def validate: ValidatedNel[InvalidItem, ResourceCreation] = {
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

// for resources
final case class ResourceMetaModification(
  status: PublishingStatus,
  editorial_right: EditorialRight,
  editor_ids: Seq[Int]
) {
  def validate(
    current: ResourceMetaModification,
    isAdmin: Boolean,
    isOwner: Boolean
  ): ValidatedNel[InvalidItem, ResourceMetaModification] = {
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
// for resource_revisions
final case class ResourceContentModification(
  title: String,
  description: String,
  url: String,
  comment: String
) {
  def validate: ValidatedNel[InvalidItem, ResourceContentModification] = {
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

final case class Resource(
  id: Int,
  revision_number: Short,
  status: PublishingStatus,
  title: String,
  description: String,
  url: String,
  comment: String,
  owner: User,
  editor: Option[User],
  tags: Seq[String],
  created_at: DateTime,
  updated_at: DateTime
)

object Resource {
  def apply(
    resource: ResourceRow,
    owner: UserRow,
    tags: Seq[String],
    revision: ResourceRevisionRow,
    editor: Option[UserRow]
  ): Resource = Resource(
    id = resource.id,
    revision_number = revision.revisionNumber,
    status = PublishingStatus.from(resource.status).get,
    title = revision.title,
    description = revision.description,
    url = revision.url,
    comment = revision.comment,
    owner = User(owner),
    editor = editor.map(User.apply),
    tags = tags,
    created_at = resource.createdAt,
    updated_at = revision.createdAt
  )
}
