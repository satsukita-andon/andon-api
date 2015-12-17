package andon.api.util

import com.typesafe.config.ConfigFactory
import io.circe._, generic.auto._, syntax._
import pdi.jwt.{ Jwt, JwtAlgorithm }
import pdi.jwt.algorithms.JwtHmacAlgorithm

final case class Token(
  userId: Int,
  login: String,
  admin: Boolean,
  suspended: Boolean
)

object Token {

  private val conf = ConfigFactory.load()
  private val key = conf.getString("jwt.secretKey")
  private val algo: JwtHmacAlgorithm = {
    JwtAlgorithm.optionFromString(conf.getString("jwt.algorithm")) match {
      case Some(algo: JwtHmacAlgorithm) => algo
      case _ => throw new ClassCastException("config `jwt.algorithm` must be JwtHmacAlgorithm")
    }
  }

  def decode(str: String): Option[Token] = {
    Jwt.decode(str, key, Seq(algo)).toOption.flatMap { claim =>
      parse.decode[Token](claim).toOption
    }
  }

  def encode(token: Token): String = {
    Jwt.encode(token.asJson.noSpaces, key, algo)
  }
}
