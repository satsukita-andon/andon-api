package andon.api.util

import cats.data.Xor
import scalikejdbc._

sealed abstract class SortOrder {
  def sql[A](s: SQLBuilder[A]): SQLBuilder[A]
  def sql(s: SQLSyntax): SQLSyntax
}
final case object DESC extends SortOrder {
  def sql[A](s: SQLBuilder[A]): SQLBuilder[A] = s.append(SQLSyntax.desc)
  def sql(s: SQLSyntax): SQLSyntax = s.desc
}
final case object ASC extends SortOrder {
  def sql[A](s: SQLBuilder[A]): SQLBuilder[A] = s.append(SQLSyntax.desc)
  def sql(s: SQLSyntax): SQLSyntax = s.asc
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
  order: Option[SortOrder] = None,
  orderBy: Option[Xor[String, SQLSyntax]] = None
) {
  def defaultLimit(limit: Int): Paging =
    this.copy(limit = this.limit.orElse(Some(limit)))
  def maxLimit(max: Int): Paging =
    this.copy(limit = this.limit.map(_.min(max)).orElse(Some(max)))
  def defaultOrder(order: SortOrder): Paging =
    this.copy(order = this.order.orElse(Some(order)))
  def defaultOrderBy(orderBy: SQLSyntax): Paging =
    this.copy(orderBy = this.orderBy.orElse(Some(Xor.right(orderBy))))
  def mapOrderBy(f: PartialFunction[String, SQLSyntax]): Paging =
    this.copy(orderBy = this.orderBy.map(_.recover[SQLSyntax](f)))
  def sql[A](sql: SQLBuilder[A]): SQLBuilder[A] = {
    val pagingSql = {
      val sorted = orderBy.map(_.fold(_ => SQLSyntax.empty, { by =>
        val selected = SQLSyntax.orderBy(by)
        order.map(_.sql(selected)).getOrElse(selected) // postgresql's default is ASC
      })).getOrElse(SQLSyntax.empty)
      val limitted = limit.map(sorted.limit).getOrElse(sorted)
      offset.map(limitted.offset).getOrElse(limitted)
    }
    sql.append(pagingSql)
  }
}
