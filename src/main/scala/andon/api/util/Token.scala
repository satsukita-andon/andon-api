package andon.api.util

import com.typesafe.config.ConfigFactory
import org.json4s._
import org.json4s.jackson.Serialization.write
import pdi.jwt.{ JwtJson4s, JwtAlgorithm }
import pdi.jwt.algorithms.JwtHmacAlgorithm
import com.github.nscala_time.time.Imports.DateTime

final case class Token(userId: Long, login: String, expirationDate: DateTime)

object Token extends JsonFormats {

  private val conf = ConfigFactory.load()
  private val key = conf.getString("jwt.secretKey")
  private val algo: JwtHmacAlgorithm = {
    JwtAlgorithm.optionFromString(conf.getString("jwt.algorithm")) match {
      case Some(algo: JwtHmacAlgorithm) => algo
      case _ => throw new ClassCastException("config `jwt.algorithm` must be JwtHmacAlgorithm")
    }
  }

  def decode(str: String): Option[Token] = {
    JwtJson4s.decodeJson(str, key, Seq(algo)).toOption.map(_.extract[Token])
  }

  def encode(token: Token): String = {
    JwtJson4s.encode(write(token), key, algo)
  }
}
