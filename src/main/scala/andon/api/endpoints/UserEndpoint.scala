package andon.api.endpoints

import io.finch._
import scalikejdbc._

import andon.api.errors._
import andon.api.jsons._
import andon.api.models._
import andon.api.util._

object UserEndpoint extends UserEndpoint {
  protected val UserModel = andon.api.models.UserModel
  protected val ClassModel = andon.api.models.ClassModel
}
trait UserEndpoint extends EndpointBase {

  protected val UserModel: UserModel
  protected val ClassModel: ClassModel

  val name = "users"
  def all = findByLogin

  private def getClass(t: Short, g: Short, c: Option[Short])(implicit s: DBSession) = {
    c.flatMap { c =>
      ClassModel.findWithPrizesAndTags(ClassId(OrdInt(t), g, c))
    }
  }

  val findByLogin: Endpoint[DetailedUser] = get(ver / name / string("login")) { login: String =>
    DB.readOnly { implicit s =>
      UserModel.findByLogin(login).map { user =>
        val first = getClass((user.times - 2).toShort, 1, user.classFirst)
        val second = getClass((user.times - 1).toShort, 2, user.classSecond)
        val third = getClass(user.times, 3, user.classThird)
        Ok(DetailedUser(user, first, second, third))
      }.getOrElse(NotFound(ResourceNotFound()))
    }
  }
}
