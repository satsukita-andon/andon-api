package andon.api.models

import scalikejdbc._

import andon.api.util.OrdInt

case class Festival(times: Short)

object Festivals {
  def all(implicit s: DBSession): Seq[Festival] = ???
  def findByTimes(times: OrdInt)(implicit s: DBSession): Option[Festival] = ???
}
