package andon.api.jsons

import org.joda.time.DateTime

import andon.api.models.generated.{
  ClassReview => ClassReviewRow,
  User => UserRow
}
import andon.api.util._

final case class ClassReview(
  id: Int,
  classId: Short,
  user: User,
  title: String,
  body: String,
  score: Option[BigDecimal],
  status: PublishingStatus,
  created_at: DateTime,
  updated_at: DateTime
)

object ClassReview {
  def apply(review: ClassReviewRow, user: UserRow): ClassReview = ClassReview(
    id = review.id,
    classId = review.classId,
    user = User(user),
    title = review.title,
    body = review.body,
    score = review.score,
    status = PublishingStatus.unsafeFrom(review.status),
    created_at = review.createdAt,
    updated_at = review.updatedAt
  )
}
