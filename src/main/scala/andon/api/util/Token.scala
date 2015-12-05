package andon.api.util

import org.json4s._
import org.json4s.jackson.Serialization.{read, write}
import pdi.jwt.{ JwtJson4s, JwtAlgorithm }

final case class Token(userId: Long, username: String, expirationDate: Long)

object Token {

  val key = "secret" // FIXME
  val algo = JwtAlgorithm.HS256

  implicit val formats = DefaultFormats

  def decode(str: String): Option[Token] = {
    JwtJson4s.decodeJson(str, key, Seq(algo)).toOption.map(_.extract[Token])
  }

  def encode(token: Token): String = {
    JwtJson4s.encode(write(token), key, algo)
  }
}
