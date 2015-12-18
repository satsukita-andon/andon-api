package andon.api.jsons

import org.joda.time.DateTime

import andon.api.models.generated.{
  Class => ClassRow,
  Prize => PrizeRow
}

final case class Class(
  id: Short,
  times: Short,
  times_ord: String,
  grade: Short,
  `class`: Short,
  title: String,
  title_kana: String,
  description: String,
  score: BigDecimal,
  header_image_url: Option[String],
  thumbnail_url: Option[String],
  prizes: Seq[Prize],
  tags: Seq[String],
  created_at: DateTime,
  updated_at: DateTime
)

object Class {
  def apply(c: ClassRow, prizes: Seq[PrizeRow]): Class = {
    ???
  }
}
