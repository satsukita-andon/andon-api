package andon.api.jsons

import org.joda.time.DateTime

import andon.api.models.generated.{ ClassImage => ClassImageRow, User => UserRow }

final case class ClassImage(
  id: Int,
  class_id: Short,
  user: User,
  raw_url: String,
  fullsize_url: String,
  thumbnail_url: String,
  created_at: DateTime
)

object ClassImage {
  def apply(image: ClassImageRow, user: UserRow): ClassImage = ClassImage(
    id = image.id,
    class_id = image.classId,
    user = User(user),
    raw_url = image.rawUrl,
    fullsize_url = image.fullsizeUrl,
    thumbnail_url = image.thumbnailUrl,
    created_at = image.createdAt
  )
}
