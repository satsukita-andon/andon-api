package andon.api

import scala.util.control.NonFatal

import akka.http.scaladsl.server.{ ExceptionHandler, Directives }
import Directives._

import andon.api.util.{ Errors, JsonSupport }
import andon.api.routes._

object Routes extends JsonSupport {

  def route(version: String) = {
    handleExceptions(exceptionHandler) {
      pathPrefix(version) {
        ArticleRoutes.route ~
        ClassDataRoutes.route ~
        GalleryRoutes.route ~
        FestivalRoutes.route ~
        ReviewRoutes.route
      } ~
      complete {
        // catch-all
        Errors.ApiNotFound
      }
    }
  }

  private val exceptionHandler = ExceptionHandler {
    case NonFatal(e) => complete(Errors.Unexpected(e))
  }
}
