package andon.api.jsons

import andon.api.models.generated.{ Festival => FestivalRow }
import andon.api.util.OrdInt

final case class Festival(
  times: Short,
  times_ord: String,
  theme: String,
  theme_kana: String,
  theme_roman: String,
  thumbnail_url: Option[String]
)

object Festival {
  def apply(fes: FestivalRow): Festival = Festival(
    times = fes.times,
    times_ord = OrdInt(fes.times).toString,
    theme = fes.theme,
    theme_kana = fes.themeKana,
    theme_roman = fes.themeRoman,
    thumbnail_url = fes.thumbnailUrl
  )
}

final case class FestivalCreation(
  times: Short,
  theme: String,
  theme_kana: String,
  theme_roman: String,
  thumbnail_url: Option[String]
)
