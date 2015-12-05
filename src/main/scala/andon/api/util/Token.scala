package andon.api.util

import com.typesafe.config.ConfigFactory
import org.json4s._
import org.json4s.jackson.Serialization.write
import pdi.jwt.{ JwtJson4s, JwtAlgorithm }
import pdi.jwt.algorithms.JwtHmacAlgorithm

final case class Token(userId: Long, username: String, expirationDate: Long)

object Token {

  private val conf = ConfigFactory.load()
  private val key = conf.getString("jwt.secretKey")
  private val algo: JwtHmacAlgorithm = {
    JwtAlgorithm.optionFromString(conf.getString("jwt.algorithm")) match {
      case Some(algo: JwtHmacAlgorithm) => algo
      case _ => throw new ClassCastException("config `jwt.algorithm` must be JwtHmacAlgorithm")
    }
  }

  implicit val formats = DefaultFormats

  def decode(str: String): Option[Token] = {
    JwtJson4s.decodeJson(str, key, Seq(algo)).toOption.map(_.extract[Token])
  }

  def encode(token: Token): String = {
    JwtJson4s.encode(write(token), key, algo)
  }
}
