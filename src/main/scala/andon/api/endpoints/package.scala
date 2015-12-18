package andon.api

import scala.util.Try
import io.finch._
import io.circe._, generic.auto._, syntax._
import shapeless.HNil

import andon.api.errors._
import andon.api.util.OrdInt
import andon.api.jsons.ErrorResponse

package object endpoints {
  private def cast[S, T <: S](s: S, t: T): S = t
  val notFound: Endpoint[Unit] = * { cast(Ok(()), NotFound(ApiNotFound())) }

  val all = (
    UserEndpoint.all :+:
      AuthEndpoint.all :+:
      FestivalEndpoint.all :+:
      notFound
  ).handle {
    case e: Exception => InternalServerError(e)
  }

  implicit val encodeException: Encoder[Exception] =
    Encoder.instance(e => ErrorResponse(e).asJson)

  val short: Endpoint[Short] = Extractor("short", s => Try(s.toShort).toOption)
  val ordInt: Endpoint[OrdInt] = Extractor("ordint", OrdInt.parse)
}
