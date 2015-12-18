package andon.api.jsons

import org.joda.time.DateTime

import andon.api.models.generated.{
  Class => ClassRow,
  Prize => PrizeRow
}
import andon.api.util.OrdInt

final case class Class(
  id: Short,
  times: Short,
  times_ord: String,
  grade: Short,
  `class`: Short,
  title: String,
  title_kana: Option[String],
  description: Option[String],
  score: Option[BigDecimal],
  header_image_url: Option[String],
  thumbnail_url: Option[String],
  prizes: Seq[Prize],
  tags: Seq[String],
  created_at: DateTime,
  updated_at: DateTime
)

object Class {
  def apply(`class`: ClassRow, prizes: Seq[PrizeRow], tags: Seq[String]): Class = Class(
    id = `class`.id,
    times = `class`.times,
    times_ord = OrdInt(`class`.times).toString,
    grade = `class`.grade,
    `class` = `class`.`class`,
    title = `class`.title,
    title_kana = `class`.titleKana,
    description = `class`.description,
    score = `class`.score,
    header_image_url = `class`.headerImageUrl,
    thumbnail_url = `class`.thumbnailUrl,
    prizes = prizes.map(Prize.apply),
    tags = tags,
    created_at = `class`.createdAt,
    updated_at = `class`.updatedAt
  )
}
