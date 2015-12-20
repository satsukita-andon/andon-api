package andon.api.endpoints

import io.finch._
import io.finch.circe._
import io.circe.generic.auto._
import scalikejdbc.DB

import andon.api.errors._
import andon.api.jsons.{ AuthInfo, EncodedToken }
import andon.api.models.UserModel

object AuthEndpoint extends AuthEndpoint {
  protected val UserModel = andon.api.models.UserModel
}
trait AuthEndpoint extends EndpointBase {

  protected val UserModel: UserModel

  val name = "auth"
  def all = newToken

  val newToken: Endpoint[EncodedToken] = post(ver / name / "token" ? body.as[AuthInfo]) { info: AuthInfo =>
    DB.localTx { implicit s =>
      UserModel.findByAuthInfo(info.login, info.password).map { user =>
        Ok(EncodedToken(user))
      }.getOrElse(BadRequest(Incorrect("login name or password is incorrect.")))
    }
  }
}
