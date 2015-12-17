package andon.api.jsons

import andon.api.models.generated.{ User => UserRow }
import andon.api.util.Token

final case class AuthInfo(login: String, password: String)

final case class EncodedToken(token: String)
object EncodedToken {
  def apply(user: UserRow): EncodedToken = {
    val token = Token(
      userId = user.id,
      login = user.login,
      admin = user.admin,
      suspended = user.suspended
    )
    EncodedToken(Token.encode(token))
  }
}
