package andon.api.endpoints

import io.finch._
import io.finch.circe._
import io.circe.generic.auto._
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
  def all = findByLogin :+: findAll :+: create :+: update :+: updateAuthority

  private def getClass(t: Short, g: Short, c: Option[Short])(implicit s: DBSession) = {
    c.flatMap { c =>
      ClassModel.findWithPrizesAndTags(ClassId(OrdInt(t), g, c))
    }
  }

  def toDetailed(user: generated.User)(implicit s: DBSession) = {
    val first = getClass((user.times - 2).toShort, 1, user.classFirst)
    val second = getClass((user.times - 1).toShort, 2, user.classSecond)
    val third = getClass(user.times, 3, user.classThird)
    DetailedUser(user, first, second, third)
  }

  def findByLogin: Endpoint[DetailedUser] = get(ver / name / string("login")) { login: String =>
    DB.readOnly { implicit s =>
      UserModel.findByLogin(login).map { user =>
        Ok(toDetailed(user))
      }.getOrElse(NotFound(ResourceNotFound()))
    }
  }

  def findAll: Endpoint[Items[User]] = get(ver / name ? paging()) { paging: Paging =>
    DB.readOnly { implicit s =>
      val p = paging.defaultLimit(50).maxLimit(100)
        .defaultOrder(UserModel.u.id -> ASC)
      val users = UserModel.findAll(p).map(User.apply)
      val all = UserModel.countAll
      Ok(Items(
        count = users.length.toLong,
        all_count = all,
        items = users
      ))
    }
  }

  def create: Endpoint[DetailedUserWithToken] = post(ver / name ? body.as[UserCreation]) { creation: UserCreation =>
    DB.localTx { implicit s =>
      val logins = UserModel.findAllLogin
      val upper = SatsukitaInfo.firstGradeTimes
      creation.validate(logins, upper).toXor.fold(
        errors => BadRequest(ValidationError(errors)),
        creation => {
          val user = UserModel.create(
            login = creation.login,
            password = creation.password,
            name = creation.name,
            times = OrdInt(creation.times)
          )
          Ok(DetailedUserWithToken(toDetailed(user)))
        }
      )
    }
  }

  def update: Endpoint[DetailedUser] = put(
    ver ? token ? body.as[UserModification]
  ) { (token: Token, modification: UserModification) =>
    DB.localTx { implicit s =>
      val logins = UserModel.findAllLogin
      modification.validate(logins).toXor.fold(
        errors => BadRequest(ValidationError(errors)),
        modification => {
          token.withUser { user =>
            UserModel.update(
              userId = user.id,
              login = modification.login,
              name = modification.name,
              biography = modification.biography,
              classFirst = modification.class_first,
              classSecond = modification.class_second,
              classThird = modification.class_third,
              chiefFirst = modification.chief_first,
              chiefSecond = modification.chief_second,
              chiefThird = modification.chief_third,
              iconUrl = modification.icon_url,
              email = modification.email
            ).map { updated =>
              Ok(toDetailed(updated))
            }.getOrElse(NotFound(ResourceNotFound()))
          }
        }
      )
    }
  }

  def updateAuthority: Endpoint[DetailedUser] = put(
    ver / name / string("login") / "authority" ? token ? body.as[UserAuthorityModification]
  ) { (login: String, token: Token, modification: UserAuthorityModification) =>
    DB.localTx { implicit s =>
      token.allowedOnly(Right.Admin) { user =>
        UserModel.updateAuthority(
          login = login,
          admin = modification.admin,
          suspended = modification.suspended
        ).map { updated =>
          Ok(toDetailed(updated))
        }.getOrElse(NotFound(ResourceNotFound()))
      }
    }
  }
}
