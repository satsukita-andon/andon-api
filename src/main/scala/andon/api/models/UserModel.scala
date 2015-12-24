package andon.api.models

import scalikejdbc._

import generated.User
import andon.api.util.PasswordUtil

object UserModel extends UserModel
trait UserModel {

  def opt(u: SyntaxProvider[User])(rs: WrappedResultSet): Option[User] =
    rs.intOpt(u.resultName.id).map(_ => User(u)(rs))

  // compare by lower case string
  def findByLogin(login: String)(implicit s: DBSession): Option[User] = {
    User.findBy(SQLSyntax.eq(User.u.login.lower, login.toLowerCase))
  }

  def findByAuthInfo(login: String, password: String)(implicit s: DBSession): Option[User] = {
    findByLogin(login).flatMap { user =>
      // 1. try using bcrypt
      //   - success: return user object
      //   - failure: next
      // 2. try using sha1
      //   - success: save bcrypt-ed password and return user object
      //   - failure: return None
      if (PasswordUtil.check(password, user.password)) {
        Some(user)
      } else {
        if (PasswordUtil.checkSha1(password, user.password)) {
          Some(user.copy(password = PasswordUtil.encrypt(password)).save)
        } else {
          None
        }
      }
    }
  }

  def find(userId: Int)(implicit s: DBSession): Option[User] = User.find(userId)
}
