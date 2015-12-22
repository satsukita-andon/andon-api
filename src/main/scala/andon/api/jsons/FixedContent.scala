package andon.api.jsons

import org.joda.time.DateTime

import andon.api.models.generated.{
  FixedContent => FixedContentRow,
  FixedContentRevision => FixedContentRevisionRow
}

final case class FixedContent(
  id: Int,
  revision_number: Short,
  body: String,
  comment: String,
  updated_by: Option[Int],
  updated_at: DateTime
)

object FixedContent {
  def apply(content: FixedContentRow, revision: FixedContentRevisionRow): FixedContent = FixedContent(
    id = content.id,
    revision_number = revision.revisionNumber,
    body = revision.body,
    comment = revision.comment,
    updated_by = revision.userId,
    updated_at = revision.createdAt
  )
}
