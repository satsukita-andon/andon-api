package andon.api.routes

import scala.util.Try

import akka.http.scaladsl.server.Directives._

import andon.api.util.Json4sJacksonSupport
import andon.api.controllers.FestivalController

object FestivalRoutes extends Json4sJacksonSupport {
  import andon.api.util.Token
  def route = {
    pathPrefix("festivals") {
      pathEnd {
        get { // GET /festivals
          complete {
            FestivalController.all
          }
        }
      } ~
      path(OrdIntMatcher) { t =>
        get { // GET /festivals/:times
          complete {
            FestivalController.detail(t) // also return prize info
          }
        }
      }
    }
  }
}
