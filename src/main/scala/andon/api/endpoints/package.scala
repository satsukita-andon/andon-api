package andon.api

import scala.util.Try
import io.finch._
import io.circe._, generic.auto._, syntax._

import andon.api.util.OrdInt
import andon.api.jsons.ErrorResponse

package object endpoints {

  val all = (
    UserEndpoint.all :+:
    AuthEndpoint.all
  )

  implicit val encodeException: Encoder[Exception] =
    Encoder.instance(e => ErrorResponse(e).asJson)

  val short: Endpoint[Short] = Extractor("short", s => Try(s.toShort).toOption)
  val ordInt: Endpoint[OrdInt] = Extractor("ordint", OrdInt.parse)
}
