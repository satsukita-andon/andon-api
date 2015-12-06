package andon.api.routes

import scala.concurrent.Future

import akka.http.scaladsl.server._, Directives._
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller

import andon.api.util.{ Token, Errors, JsonSupport }

object WrapperDirectives extends JsonSupport {

  def jsonEntity[T](f: T => Route)(implicit um: FromRequestUnmarshaller[T]) = {
    jsonEntityOrElse[T](f)(um) ~ complete(Errors.JsonError)
  }

  def jsonEntityOrElse[T](f: T => Route)(implicit um: FromRequestUnmarshaller[T]) = {
    entity(as[T](um))(f)
  }

  def auth(f: Token => Route) = {
    authOrElse(f) ~ complete(Errors.Unauthorized) // TODO: NoHeaderError
  }

  def authOrElse(f: Token => Route) = {
    headerValueByName("X-Andon-Authorization") { str =>
      Token.decode(str) match {
        case Some(token) => f(token)
        case None => complete(Errors.Unauthorized)
      }
    }
  }
}
