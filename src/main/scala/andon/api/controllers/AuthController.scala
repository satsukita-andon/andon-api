package andon.api.controllers

import com.roundeights.hasher.Hasher
import com.github.nscala_time.time.Imports._

import andon.api.models.Users
import andon.api.util.{ Errors, Token }

object AuthJsons {
  final case class AuthInfo(login: String, password: String)
  final case class EncodedToken(token: String)
}

trait AuthController {

  import AuthJsons._

  private def newToken(userId: Long, login: String): EncodedToken = {
    val token = Token(userId, login, (DateTime.now + 7.days).getMillis) // TODO: do not use magic number `7`
    EncodedToken(Token.encode(token))
  }

  def getToken(info: AuthInfo): Either[Errors.Error, EncodedToken] = {
    Users.findByLogin(info.login).map { user =>
      if (user.password == Hasher(info.password).bcrypt) { // TODO: move the code using hash function to UserModel
        Right(newToken(user.id, user.login))
      } else {
        Left(Errors.Unauthorized)
      }
    }.getOrElse(Left(Errors.ResourceNotFound))
  }

  def refleshToken(token: Token): EncodedToken = {
    newToken(token.userId, token.login)
  }
}

object AuthController extends AuthController
