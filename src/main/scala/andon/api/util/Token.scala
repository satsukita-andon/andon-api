package andon.api.util

import com.typesafe.config.ConfigFactory
import io.finch._
import io.circe._, generic.auto._, syntax._
import pdi.jwt.{ Jwt, JwtAlgorithm }
import pdi.jwt.algorithms.JwtHmacAlgorithm
import scalikejdbc.DBSession

import andon.api.errors._
import andon.api.models.generated.User

final case class Token(
  userId: Int
) {
  // The reason why getting user from DB (not including info in token) for each time is that,
  // access-rights might be changed
  def allowedOnly[A](rights: Right*)(f: User => Output[A])(implicit s: DBSession) = {
    partition(rights, f, _ => Forbidden(NoPermission()))
  }

  def rejectedOnly[A](rights: Right*)(f: User => Output[A])(implicit s: DBSession) = {
    partition(rights, _ => Forbidden(NoPermission()), f)
  }

  private def partition[A](
    rights: Seq[Right],
    matched: User => Output[A],
    unmatched: User => Output[A]
  )(implicit s: DBSession): Output[A] = {
    User.find(userId).map { user =>
      val b = rights.exists {
        case Right.Admin => user.admin
        case Right.Suspended => user.suspended
        case Right.CohortOf(t) => user.times == t
        case Right.ClassmateOf(t, 1, c) =>
          user.times == t && user.classFirst == Some(c)
        case Right.ClassmateOf(t, 2, c) =>
          user.times == t && user.classSecond == Some(c)
        case Right.ClassmateOf(t, 3, c) =>
          user.times == t && user.classThird == Some(c)
        case Right.ClassmateOf(_, _, _) => false
        case Right.ChiefOf(t, 1, c) =>
          user.times == t && user.classFirst == Some(c) && user.chiefFirst == Some(true)
        case Right.ChiefOf(t, 2, c) =>
          user.times == t && user.classSecond == Some(c) && user.chiefSecond == Some(true)
        case Right.ChiefOf(t, 3, c) =>
          user.times == t && user.classThird == Some(c) && user.chiefThird == Some(true)
        case Right.ChiefOf(_, _, _) => false
      }
      if (b) matched(user) else unmatched(user)
    }.getOrElse(NotFound(ResourceNotFound("You are a deleted user.")))
  }
}

sealed abstract class Right
object Right {
  case object Admin extends Right
  case object Suspended extends Right
  case class CohortOf(times: Short) extends Right
  // if classmate then cohort
  case class ClassmateOf(times: Short, grade: Short, `class`: Short) extends Right
  // if chief then classmate
  case class ChiefOf(times: Short, grade: Short, `class`: Short) extends Right
}

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
