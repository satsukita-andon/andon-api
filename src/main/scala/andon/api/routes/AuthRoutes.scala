package andon.api.routes

import akka.http.scaladsl.server.Directives._

import andon.api.controllers.{ AuthController, AuthJsons }
import andon.api.util.{ Json4sJacksonSupport }
import WrapperDirectives._

object AuthRoutes extends Json4sJacksonSupport {
  def route = {
    pathPrefix("auth") {
      path("token") {
        get {
          authOrElse { token =>
            complete {
              AuthController.refleshToken(token)
            }
          } ~
          jsonEntity[AuthJsons.AuthInfo] { info =>
            complete {
              AuthController.getToken(info)
            }
          }
        }
      }
    }
  }
}
