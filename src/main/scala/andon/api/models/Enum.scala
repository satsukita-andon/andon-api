package andon.api.models

import andon.api.util.InjectiveTypeBinder

sealed abstract class PublishingStatus(val code: String)
object PublishingStatus extends InjectiveTypeBinder[PublishingStatus, String] {
  case object Private extends PublishingStatus("private")
  case object Published extends PublishingStatus("published")
  case object Suspended extends PublishingStatus("suspended")
  def mapping(s: PublishingStatus) = s.code
  val all = Seq(Private, Published, Suspended)
}

sealed abstract class EditorialRight(val code: String)
object EditorialRight extends InjectiveTypeBinder[EditorialRight, String] {
  case object Selected extends EditorialRight("selected")
  case object Classmate extends EditorialRight("classmate")
  case object Cohort extends EditorialRight("cohort")
  case object All extends EditorialRight("all")
  def mapping(e: EditorialRight) = e.code
  val all = Seq(Selected, Classmate, Cohort, All)
}

sealed abstract class FixedContentType(val code: String)
object FixedContentType extends InjectiveTypeBinder[FixedContentType, String] {
  case object News extends FixedContentType("news")
  case object About extends FixedContentType("about")
  case object Contact extends FixedContentType("contact")
  def mapping(e: FixedContentType) = e.code
  val all = Seq(News, About, Contact)
}
