package andon.api.models

import scalikejdbc._

import andon.api.util.OrdInt
import generated.Festival

object FestivalModel {
  def all(implicit s: DBSession): Seq[Festival] = ???
  def findByTimes(times: OrdInt)(implicit s: DBSession): Option[Festival] = ???
  def create(
    times: Short,
    theme: String,
    themeRoman: String,
    themeKana: String,
    thumbnailUrl: Option[String] = None
  )(implicit session: DBSession): Festival = Festival.create(
    times = times,
    theme = theme,
    themeRoman = themeRoman,
    themeKana = themeKana,
    thumbnailUrl = thumbnailUrl
  )
}
