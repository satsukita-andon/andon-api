package andon.api

import io.finch._
import io.circe._

import andon.api.errors._

package object endpoints {
  private def cast[S, T <: S](s: S, t: T): S = t
  val notFound: Endpoint[Unit] = * { cast(Ok(()), NotFound(ApiNotFound())) }

  def all = (
    UserEndpoint.all :+:
      AuthEndpoint.all :+:
      FestivalEndpoint.all :+:
      ClassEndpoint.all :+:
      ClassArticleEndpoint.all :+:
      ClassResourceEndpoint.all :+:
      ArticleEndpoint.all :+:
      ResourceEndpoint.all :+:
      FixedContentEndpoint.all :+:
      FileEndpoint.all :+:
      OtherEndpoint.all :+:
      notFound
  ).handle {
    case e: TokenRequired => Unauthorized(e)
    case e: Exception => InternalServerError(e)
    case e => InternalServerError(Unexpected(e.toString))
  }

  implicit val encodeException: Encoder[Exception] =
    Encoder.instance(AndonError(_).toJson)
}
