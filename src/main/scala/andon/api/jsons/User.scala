package andon.api.jsons

import andon.api.models.generated.{ User => UserRow }

final case class User(
  id: Int,
  login: String
)

object User {
  def apply(user: UserRow): User = User(
    id = user.id,
    login = user.login
  )
}
