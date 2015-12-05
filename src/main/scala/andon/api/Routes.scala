package andon.api

import scala.concurrent.ExecutionContext
import scala.util.Try
import scala.util.control.NonFatal

import akka.stream.ActorMaterializer
import akka.http.scaladsl.server._, Directives._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.marshalling.Marshaller._
import akka.http.scaladsl.unmarshalling.Unmarshaller._

import andon.api.util.{ Errors, OrdIntMatcher, SignedIntNumber, Json4sJacksonSupport }
import andon.api.controllers._

object Routes extends Json4sJacksonSupport {

  def route(version: String)(implicit ec: ExecutionContext, fm: ActorMaterializer): Route = {
    val exceptionHandler = ExceptionHandler {
      case NonFatal(e) => complete {
        Errors.Unexpected(e)
      }
    }
    handleExceptions(exceptionHandler) {
      pathPrefix(version) {
        articles ~ classData ~ gallery ~ festivals ~ reviews
      } ~
      complete {
        // catch-all
        Errors.ApiNotFound
      }
    }
  }

  private def articles(implicit ec: ExecutionContext, fm: ActorMaterializer): Route = {
    pathPrefix("articles") {
      pathEnd {
        get {
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
        post {
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
          get {
            complete {
              ArticleController.get(id)
            }
          }
        } ~
        pathPrefix("commits") {
          pathEnd {
            get {
              complete {
                ArticleController.commits(id)
              }
            }
          } ~
          path(Rest) { commitId =>
            get {
              complete {
                ArticleController.commit(id, commitId)
              }
            }
          }
        }
      }
    }
  }

  private def classData(implicit ec: ExecutionContext, fm: ActorMaterializer): Route = {
    pathPrefix("classes" / OrdIntMatcher) { t =>
      pathEnd {
        get {
          complete {
            ClassDataController.getTimes(t)
          }
        }
      } ~
      pathPrefix(IntNumber) { g =>
        pathEnd {
          get {
            complete {
              ClassDataController.getGrade(t, g)
            }
          }
        } ~
        path(SignedIntNumber) { c =>
          get {
            complete {
              ClassDataController.getClass(t, g, c)
            }
          }
        }
      }
    }
  }

  private def gallery(implicit ec: ExecutionContext, fm: ActorMaterializer): Route = {
    path("gallery" / OrdIntMatcher / IntNumber / SignedIntNumber) { case (t, g, c) =>
      get {
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

  private def festivals(implicit ec: ExecutionContext, fm: ActorMaterializer): Route = {
    pathPrefix("festivals") {
      pathEnd {
        get {
          complete {
            FestivalController.all
          }
        }
      } ~
      path(OrdIntMatcher) { t =>
        get {
          complete {
            FestivalController.detail(t) // also return prize info
          }
        }
      }
    }
  }

  private def reviews(implicit ec: ExecutionContext, fm: ActorMaterializer): Route = {
    path("reviews" / OrdIntMatcher / IntNumber / SignedIntNumber) { (t, g, c) =>
      get {
        complete {
          ReviewController.all(t, g, c)
        }
      }
    }
  }
}
