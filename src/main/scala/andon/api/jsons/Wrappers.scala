package andon.api.jsons

final case class Items[A](
  all_count: Long,
  count: Long,
  items: Seq[A]
)
