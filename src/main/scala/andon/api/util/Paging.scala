package andon.api.util

import cats.data.Xor
import scalikejdbc._

sealed abstract class SortOrder {
  def sql[A](s: SQLBuilder[A]): SQLBuilder[A]
  def sql(s: SQLSyntax): SQLSyntax
}
final case object DESC extends SortOrder {
  def sql[A](s: SQLBuilder[A]): SQLBuilder[A] = s.append(sqls.desc)
  def sql(s: SQLSyntax): SQLSyntax = s.desc
}
final case object ASC extends SortOrder {
  def sql[A](s: SQLBuilder[A]): SQLBuilder[A] = s.append(sqls.desc)
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
  order: Option[Seq[SortOrder]] = None,
  orderBy: Option[Xor[String, Seq[SQLSyntax]]] = None
) {
  def defaultLimit(limit: Int): Paging =
    this.copy(limit = this.limit.orElse(Some(limit)))
  def maxLimit(max: Int): Paging =
    this.copy(limit = this.limit.map(_.min(max)).orElse(Some(max)))
  def defaultOrder(order: SortOrder*): Paging =
    this.copy(order = this.order.orElse(Some(order)))
  def defaultOrderBy(orderBy: SQLSyntax*): Paging =
    this.copy(orderBy = this.orderBy.orElse(Some(Xor.right(orderBy))))
  def mapOrderBy(f: PartialFunction[String, SQLSyntax]): Paging =
    this.copy(orderBy = this.orderBy.map(_.recover[Seq[SQLSyntax]](PartialFunction(_.split(",").collect(f)))))
  def sql[A](sql: SQLBuilder[A]): SQLBuilder[A] = {
    val pagingSql = {
      val sorted = orderBy.map(_.fold(_ => sqls.empty, { by =>
        val len = by.length
        val orders = order.map(_.map(Some.apply).padTo(len, None))
          .getOrElse(Seq.fill(by.length)(None))
        val ss = by.zip(orders).map { case (b, o) => o.fold(b)(_.sql(b)) } // postgresql's default is ASC
        sqls.orderBy(ss: _*)
      })).getOrElse(sqls.empty)
      val limitted = limit.map(sorted.limit).getOrElse(sorted)
      offset.map(limitted.offset).getOrElse(limitted)
    }
    sql.append(pagingSql)
  }
}
