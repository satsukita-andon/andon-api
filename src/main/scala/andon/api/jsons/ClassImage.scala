package andon.api.jsons

import org.joda.time.DateTime

import andon.api.models.generated.{ ClassImage => ClassImageRow, User => UserRow }

final case class ClassImage(
  id: Int,
  class_id: Short,
  user: User,
  url: String,
  created_at: DateTime
)

object ClassImage {
  def apply(image: ClassImageRow, user: UserRow): ClassImage = ClassImage(
    id = image.id,
    class_id = image.classId,
    user = User(user),
    url = image.url,
    created_at = image.createdAt
  )
}
