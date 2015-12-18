package andon.api.util

sealed abstract class PublishingStatus(val code: String) {
  override def toString = code
}
object PublishingStatus extends Injective[PublishingStatus, String] {
  case object Private extends PublishingStatus("private")
  case object Published extends PublishingStatus("published")
  case object Suspended extends PublishingStatus("suspended")
  def to(s: PublishingStatus) = s.code
  val all: Set[PublishingStatus] = Set(Private, Published, Suspended)
}

sealed abstract class EditorialRight(val code: String) {
  override def toString = code
}
object EditorialRight extends Injective[EditorialRight, String] {
  case object Selected extends EditorialRight("selected")
  case object Classmate extends EditorialRight("classmate")
  case object Cohort extends EditorialRight("cohort")
  case object All extends EditorialRight("all")
  def to(e: EditorialRight) = e.code
  val all: Set[EditorialRight] = Set(Selected, Classmate, Cohort, All)
}

sealed abstract class FixedContentType(val code: String) {
  override def toString = code
}
object FixedContentType extends Injective[FixedContentType, String] {
  case object News extends FixedContentType("news")
  case object About extends FixedContentType("about")
  case object Contact extends FixedContentType("contact")
  def to(e: FixedContentType) = e.code
  val all: Set[FixedContentType] = Set(News, About, Contact)
}
