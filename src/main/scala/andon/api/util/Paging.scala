package andon.api.util

import scalikejdbc._

final case class Paging(
  offset: Option[Int],
  limit: Option[Int]
    // DESC or ASC
    // order by _
) {
  def defaultLimit(limit: Int): Paging =
    this.copy(limit = this.limit.orElse(Some(limit)))
  def applyDefault(offset: Option[Int] = None, limit: Option[Int] = None): Paging =
    Paging(
      offset = this.offset.orElse(offset),
      limit = this.limit.orElse(limit)
    )
  def sql[A](s: => PagingSQLBuilder[A]): PagingSQLBuilder[A] = {
    val limitted = limit.map(s.limit).getOrElse(s)
    offset.map(limitted.offset).getOrElse(limitted)
  }
}
