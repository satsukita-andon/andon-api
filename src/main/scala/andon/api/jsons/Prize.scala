package andon.api.jsons

import andon.api.models.generated.{ Prize => PrizeRow }

final case class Prize(
  id: Short,
  code: String,
  label: String,
  index: Short,
  color: String
)

object Prize {
  def apply(prize: PrizeRow): Prize = Prize(
    id = prize.id,
    code = prize.code,
    label = prize.label,
    index = prize.index,
    color = prize.color
  )
}
