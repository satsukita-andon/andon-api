package andon.api.jsons

import org.joda.time.DateTime

import andon.api.models.generated.{ ClassImage => ClassImageRow, User => UserRow }

final case class ClassImage(
  id: Int,
  class_id: Short,
  user: User,
  rawUrl: String,
  fullsizeUrl: String,
  thumbnailUrl: String,
  created_at: DateTime
)

object ClassImage {
  def apply(image: ClassImageRow, user: UserRow): ClassImage = ClassImage(
    id = image.id,
    class_id = image.classId,
    user = User(user),
    rawUrl = image.rawUrl,
    fullsizeUrl = image.fullsizeUrl,
    thumbnailUrl = image.thumbnailUrl,
    created_at = image.createdAt
  )
}
