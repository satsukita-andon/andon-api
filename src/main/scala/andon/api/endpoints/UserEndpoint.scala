package andon.api.endpoints

import io.finch._
import scalikejdbc.DB

import andon.api.errors._
import andon.api.jsons.User
import andon.api.models.UserModel

object UserEndpoint extends UserEndpoint {
  protected val UserModel = andon.api.models.UserModel
}
trait UserEndpoint extends EndpointBase {

  protected val UserModel: UserModel

  val name = "users"
  def all = findByLogin

  val findByLogin: Endpoint[User] = get(ver / name / string("login")) { login: String =>
    DB.readOnly { implicit s =>
      UserModel.findByLogin(login).map { user =>
        Ok(User(user))
      }.getOrElse(NotFound(ResourceNotFound()))
    }
  }
}
