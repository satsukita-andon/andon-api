package andon.api.models

import scalikejdbc._

import generated.User
import andon.api.util.PasswordUtil

trait UserModel {
  def findByLogin(login: String)(implicit s: DBSession): Option[User] = {
    // compare lower case string
    ???
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
}

object UserModel extends UserModel
