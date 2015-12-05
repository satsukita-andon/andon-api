package andon.api.routes

import akka.http.scaladsl.server.Directives._

import andon.api.util.Json4sJacksonSupport
import andon.api.controllers.ClassDataController

object ClassDataRoutes extends Json4sJacksonSupport {

  def route = {
    pathPrefix("classes" / OrdIntMatcher) { t =>
      pathEnd {
        get { // GET /classes/:times
          complete {
            ClassDataController.getTimes(t)
          }
        }
      } ~
      pathPrefix(IntNumber) { g =>
        pathEnd {
          get { // GET /classes/:times/:grade
            complete {
              ClassDataController.getGrade(t, g)
            }
          }
        } ~
        path(SignedIntNumber) { c =>
          get { // GET /classes/:times/:grade/:class
            complete {
              ClassDataController.getClass(t, g, c)
            }
          }
        }
      }
    }
  }
}
