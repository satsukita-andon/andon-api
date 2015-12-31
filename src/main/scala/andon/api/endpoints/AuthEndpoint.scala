package andon.api.endpoints

import io.finch._
import io.finch.circe._
import io.circe.generic.auto._
import scalikejdbc.DB

import andon.api.errors._
import andon.api.jsons.{ AuthInfo, EncodedToken, DetailedUser }
import andon.api.models.UserModel
import andon.api.util.Token

object AuthEndpoint extends AuthEndpoint {
  protected val UserModel = andon.api.models.UserModel
}
trait AuthEndpoint extends EndpointBase {

  protected val UserModel: UserModel

  val name = "auth"
  def all = newToken :+: me

  def newToken: Endpoint[EncodedToken] = post(ver / name / "token" ? body.as[AuthInfo]) { info: AuthInfo =>
    DB.localTx { implicit s =>
      UserModel.findByAuthInfo(info.login, info.password).map { user =>
        Ok(EncodedToken(user))
      }.getOrElse(BadRequest(Incorrect("login name or password is incorrect.")))
    }
  }

  def me: Endpoint[DetailedUser] = get(ver / name / "me" ? token) { token: Token =>
    DB.readOnly { implicit s =>
      token.withUser { user =>
        Ok(UserEndpoint.toDetailed(user))
      }
    }
  }
}
