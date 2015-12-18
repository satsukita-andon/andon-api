package andon.api.util

final case class Paging(
  offset: Option[Int],
  limit: Option[Int]
) {
  def offsetOr(n: Int) = offset.getOrElse(n)
  def limitOr(n: Int) = limit.getOrElse(n)
}
