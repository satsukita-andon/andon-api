package andon.api.endpoints

import scala.util.Try
import io.finch._

import andon.api.util.OrdInt

trait EndpointBase {
  val ver = "dev"
  val name: String

  val short: Endpoint[Short] = Extractor("short", s => Try(s.toShort).toOption)
  val ordInt: Endpoint[OrdInt] = Extractor("ordint", OrdInt.parse)
}
