package andon.api.util

import scalikejdbc._

sealed abstract class SortOrder {
  def sql[A](s: PagingSQLBuilder[A]): PagingSQLBuilder[A]
}
final case object DESC extends SortOrder {
  def sql[A](s: PagingSQLBuilder[A]): PagingSQLBuilder[A] = s.desc
}
final case object ASC extends SortOrder {
  def sql[A](s: PagingSQLBuilder[A]): PagingSQLBuilder[A] = s.asc
}
object SortOrder extends Injective[SortOrder, String] {
  def to(s: SortOrder) = s match {
    case DESC => "DESC"
    case ASC => "ASC"
  }
  val domain: Set[SortOrder] = Set(DESC, ASC)
}

final case class Paging(
  offset: Option[Int] = None,
  limit: Option[Int] = None,
  order: Option[SortOrder] = None
) {
  def defaultLimit(limit: Int): Paging =
    this.copy(limit = this.limit.orElse(Some(limit)))
  def maxLimit(max: Int): Paging =
    this.copy(limit = this.limit.map(_.min(max)).orElse(Some(max)))
  def defaultOrder(order: SortOrder): Paging =
    this.copy(order = this.order.orElse(Some(order)))
  def sql[A](s: PagingSQLBuilder[A]): PagingSQLBuilder[A] = {
    val sorted = order.map(_.sql(s)).getOrElse(s) // postgresql's default is ASC
    val limitted = limit.map(sorted.limit).getOrElse(sorted)
    offset.map(limitted.offset).getOrElse(limitted)
  }
}
