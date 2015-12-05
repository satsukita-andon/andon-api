package andon.api.routes

import scala.util.Try

import akka.http.scaladsl.server.Directives._

import andon.api.util.{ Errors, Json4sJacksonSupport }
import andon.api.controllers.{ ArticleController, ArticleJsons }

object ArticleRoutes extends Json4sJacksonSupport {

  def route = {
    pathPrefix("articles") {
      pathEnd {
        get { // GET /articles
          parameterMap { params =>
            complete {
              val offset = params.get("offset").map(s => Try(s.toInt).toOption).flatten
              val limit = params.get("limit").map(s => Try(s.toInt).toOption).flatten
              ArticleController.all(
                offset = offset,
                limit = limit
              )
            }
          }
        } ~
        post { // POST /articles
          entity(as[ArticleJsons.Create]) { article =>
            complete {
              ArticleController.add(article)
            }
          } ~
          complete {
            Errors.JsonError
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
