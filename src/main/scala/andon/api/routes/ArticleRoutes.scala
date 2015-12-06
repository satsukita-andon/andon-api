package andon.api.routes

import scala.util.Try

import akka.http.scaladsl.server.Directives._

import andon.api.util.{ Errors, JsonSupport }
import andon.api.controllers.{ ArticleController, ArticleJsons }

import WrapperDirectives._

object ArticleRoutes extends JsonSupport {

  def route = {
    pathPrefix("articles") {
      pathEnd {
        get { // GET /articles
          parameterMap { params =>
            complete {
              val offset = params.get("offset").flatMap(s => Try(s.toInt).toOption)
              val limit = params.get("limit").flatMap(s => Try(s.toInt).toOption)
              ArticleController.all(
                offset = offset,
                limit = limit
              )
            }
          }
        } ~
        post { // POST /articles
          auth { token =>
            jsonEntity[ArticleJsons.Create] { article =>
              complete {
                ArticleController.add(token.userId, article)
              }
            }
          }
        }
      } ~
      pathPrefix(LongNumber) { id =>
        pathEnd {
          get { // GET /articles/:id
            complete {
              ArticleController.get(id)
            }
          }
        } ~
        pathPrefix("commits") {
          pathEnd {
            get { // GET /articles/:id/commits
              complete {
                ArticleController.commits(id)
              }
            }
          } ~
          path(Rest) { commitId =>
            get { // GET /articles/:id/commits/:commit
              complete {
                ArticleController.commit(id, commitId)
              }
            }
          }
        }
      }
    }
  }
}
