package andon.api.endpoints

import io.finch._
import io.finch.circe._
import io.circe.generic.auto._
import scalikejdbc.DB

import andon.api.errors.AuthRequired
import andon.api.jsons.{ AuthInfo, EncodedToken }
import andon.api.models.UserModel

object AuthEndpoint extends EndpointBase {

  val name = "auth"

  val newToken: Endpoint[EncodedToken] = post(ver / name / "token" ? body.as[AuthInfo]) { info: AuthInfo =>
    DB.localTx { implicit s =>
      UserModel.findByAuthInfo(info.login, info.password).map { user =>
        Ok(EncodedToken(user))
      }.getOrElse(Unauthorized(AuthRequired()))
    }
  }

  val all = newToken
}
