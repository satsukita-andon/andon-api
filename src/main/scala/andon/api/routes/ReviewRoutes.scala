package andon.api.routes

import akka.http.scaladsl.server.Directives._

import andon.api.util.JsonSupport
import andon.api.controllers.ReviewController

object ReviewRoutes extends JsonSupport {
  def route = {
    path("reviews" / OrdIntMatcher / IntNumber / SignedIntNumber) { (t, g, c) =>
      get { // GET /reviews/:times/:grade/:class
        complete {
          ReviewController.all(t, g, c)
        }
      }
    }
  }
}
