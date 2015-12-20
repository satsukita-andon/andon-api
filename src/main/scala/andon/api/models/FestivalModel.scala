package andon.api.models

import cats.data.Xor
import scalikejdbc._

import andon.api.errors._
import andon.api.util._
import generated.Festival

object FestivalModel extends FestivalModel
trait FestivalModel {

  private val f = Festival.f

  def findAll(order: SortOrder)(implicit s: DBSession): Seq[Festival] = {
    withSQL {
      order.sql {
        select.from(Festival as f).orderBy(f.times)
      }
    }.map(Festival(f)).list.apply()
  }
  def findByTimes(times: OrdInt)(implicit s: DBSession): Option[Festival] = ???
  def create(
    times: OrdInt,
    theme: String,
    themeRoman: String,
    themeKana: String,
    thumbnailUrl: Option[String] = None
  )(implicit session: DBSession): Xor[AndonError, Festival] = try {
    val fes = Festival.create(
      times = times.raw,
      theme = theme,
      themeRoman = themeRoman,
      themeKana = themeKana,
      thumbnailUrl = thumbnailUrl
    )
    Xor.right(fes)
  } catch {
    case e: java.sql.SQLException => Xor.left(ResourceAlreadyExists()) // TODO: check error type
  }
}
