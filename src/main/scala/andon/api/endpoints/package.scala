package andon.api

import io.finch._
import io.circe._
import shapeless.HNil

import andon.api.errors._
import andon.api.util.OrdInt

package object endpoints {
  private def cast[S, T <: S](s: S, t: T): S = t
  val notFound: Endpoint[Unit] = * { cast(Ok(()), NotFound(ApiNotFound())) }

  val all = (
    UserEndpoint.all :+:
      AuthEndpoint.all :+:
      FestivalEndpoint.all :+:
      ClassEndpoint.all :+:
      ClassArticleEndpoint.all :+:
      FixedContentEndpoint.all :+:
      notFound
  ).handle {
    case e: TokenRequired => Unauthorized(e)
    case e: Exception => InternalServerError(e)
    case e => InternalServerError(Unexpected(e.toString))
  }

  implicit val encodeException: Encoder[Exception] =
    Encoder.instance(AndonError(_).toJson)
}
