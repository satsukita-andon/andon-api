package andon.api.endpoints

import scala.util.Try
import io.finch._

import andon.api.errors.AuthRequired
import andon.api.util.{ OrdInt, Token }

trait EndpointBase {
  val ver = "dev"
  val name: String

  val auth: RequestReader[Token] = headerOption("Authentication").flatMap {
    case None => RequestReader.exception(AuthRequired())
    case Some(str) => {
      val r = """^\s*Bearer\s+([^\s\,]*)\s*$""".r
      str match {
        case r(token) => Token.decode(token) match {
          case None => RequestReader.exception(AuthRequired())
          case Some(token) => RequestReader.value(token)
        }
        case _ => RequestReader.exception(AuthRequired())
      }
    }
  }
  val short: Endpoint[Short] = Extractor("short", s => Try(s.toShort).toOption)
  val ordInt: Endpoint[OrdInt] = Extractor("ordint", OrdInt.parse)
}
