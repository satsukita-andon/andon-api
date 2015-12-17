package andon.api

import scala.util.Try
import io.finch._

import andon.api.util.OrdInt

package object endpoints {
  val all = UserEndpoint.all

  val short: Endpoint[Short] = Extractor("short", s => Try(s.toShort).toOption)
  val ordInt: Endpoint[OrdInt] = Extractor("ordint", OrdInt.parse)
}
