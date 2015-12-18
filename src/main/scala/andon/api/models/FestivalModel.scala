package andon.api.models

import cats.data.Xor
import scalikejdbc._

import andon.api.errors._
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
  )(implicit session: DBSession): Xor[AndonError, Festival] = try {
    val fes = Festival.create(
      times = times,
      theme = theme,
      themeRoman = themeRoman,
      themeKana = themeKana,
      thumbnailUrl = thumbnailUrl
    )
    Xor.right(fes)
  } catch {
    case e: java.sql.SQLException => Xor.left(ResourceAlreadyExists())
  }
}
