package andon.api.endpoints

import io.finch._
import scalikejdbc.DB

import andon.api.errors._
import andon.api.jsons.User
import andon.api.models.UserModel

object UserEndpoint extends EndpointBase {

  val name = "users"

  val findByLogin: Endpoint[User] = get(ver / name / string("login")) { login: String =>
    DB.readOnly { implicit s =>
      UserModel.findByLogin(login).map { user =>
        Ok(User(user))
      }.getOrElse(NotFound(ResourceNotFound))
    }
  }

  val all = findByLogin
}
