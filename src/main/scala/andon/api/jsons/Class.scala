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
  def apply(c: ClassRow, prizes: Seq[PrizeRow], tags: Seq[String]): Class = Class(
    id = c.id,
    times = c.times,
    times_ord = OrdInt(c.times).toString,
    grade = c.grade,
    `class` = c.`class`,
    title = c.title,
    title_kana = c.titleKana,
    description = c.description,
    score = c.score,
    header_image_url = c.headerImageUrl,
    thumbnail_url = c.thumbnailUrl,
    prizes = prizes.map(Prize.apply),
    tags = tags,
    created_at = c.createdAt,
    updated_at = c.updatedAt
  )
}
