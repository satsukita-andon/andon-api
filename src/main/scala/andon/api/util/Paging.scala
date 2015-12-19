package andon.api.util

import scalikejdbc._

sealed abstract class SortType
final case object DESC extends SortType
final case object ASC extends SortType
object SortType extends Injective[SortType, String] {
  def to(s: SortType) = s match {
    case DESC => "DESC"
    case ASC => "ASC"
  }
  val domain: Set[SortType] = Set(DESC, ASC)
}

final case class Paging(
  offset: Option[Int] = None,
  limit: Option[Int] = None,
  order: Option[SortType] = None
) {
  def defaultLimit(limit: Int): Paging =
    this.copy(limit = this.limit.orElse(Some(limit)))
  def maxLimit(max: Int): Paging =
    this.copy(limit = this.limit.map(_.min(max)).orElse(Some(max)))
  def defaultOrder(order: SortType): Paging =
    this.copy(order = this.order.orElse(Some(order)))
  def sql[A](s: => PagingSQLBuilder[A]): PagingSQLBuilder[A] = {
    val sorted = order.map {
      case DESC => s.desc
      case ASC => s.asc
    }.getOrElse(s) // postgresql's default is ASC
    val limitted = limit.map(sorted.limit).getOrElse(sorted)
    offset.map(limitted.offset).getOrElse(limitted)
  }
}
