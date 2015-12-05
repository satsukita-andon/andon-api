package andon.api.routes

import scala.util.Try

import akka.http.scaladsl.server.Directives._

import andon.api.util.Json4sJacksonSupport
import andon.api.controllers.GalleryController

object GalleryRoutes extends Json4sJacksonSupport {

  def route = {
    path("gallery" / OrdIntMatcher / IntNumber / SignedIntNumber) { case (t, g, c) =>
      get { // GET /gallery/:times/:grade/:class
        parameterMap { params =>
          complete {
            val offset = params.get("offset").map(s => Try(s.toInt).toOption).flatten
            val limit = params.get("limit").map(s => Try(s.toInt).toOption).flatten
            GalleryController.all(t, g, c, offset, limit)
          }
        }
      }
    }
  }
}
