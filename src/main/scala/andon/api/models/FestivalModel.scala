package andon.api.models

import scalikejdbc._

import andon.api.util.OrdInt
import generated.Festival

object FestivalModel {
  def all(implicit s: DBSession): Seq[Festival] = ???
  def findByTimes(times: OrdInt)(implicit s: DBSession): Option[Festival] = ???
}
