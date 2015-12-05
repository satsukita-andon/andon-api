package andon.api.routes

import scala.concurrent.Future

import akka.http.scaladsl.server._, Directives._
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller

import andon.api.util.{ Token, Errors, Json4sJacksonSupport }

object WrapperDirectives extends Json4sJacksonSupport {

  def jsonEntity[T](f: T => Route)(implicit um: FromRequestUnmarshaller[T]) = {
    entity(as[T](um))(f) ~ complete(Errors.JsonError)
  }

  def auth(f: Token => Route) = {
    headerValueByName("X-Andon-Authorization") { str =>
      Token.decode(str) match {
        case Some(token) => f(token)
        case None => complete(Errors.Unauthorized)
      }
    } ~ complete(Errors.Unauthorized)
  }
}
