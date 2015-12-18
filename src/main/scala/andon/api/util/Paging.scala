package andon.api.util

import scalikejdbc._

final case class Paging(
  offset: Option[Int],
  limit: Option[Int]
    // DESC or ASC
    // order by _
) {
  def applyDefault(offset: Option[Int], limit: Option[Int]): Paging =
    Paging(
      offset = this.offset.orElse(offset),
      limit = this.limit.orElse(limit)
    )
  def sql[A](s: => PagingSQLBuilder[A]): PagingSQLBuilder[A] = {
    val limitted = limit.map(s.limit).getOrElse(s)
    offset.map(limitted.offset).getOrElse(limitted)
  }
}
