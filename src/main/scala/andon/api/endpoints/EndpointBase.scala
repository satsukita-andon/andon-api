package andon.api.endpoints

import scala.util.Try
import io.finch._

import andon.api.errors._
import andon.api.util.{ OrdInt, Token }

trait EndpointBase {
  val ver = "dev"
  val name: String

  // must be handle exception to cast status-code to 401 Unauthorized
  val token: RequestReader[Token] = headerOption("Authentication").flatMap { header =>
    val r = """^\s*Bearer\s+([^\s\,]*)\s*$""".r
    val tokenOpt = for {
      str <- header
      tokenStr <- r.unapplySeq(str).flatMap(_.headOption)
      token <- Token.decode(tokenStr)
    } yield token
    tokenOpt match {
      case None => RequestReader.exception(TokenRequired())
      case Some(token) => RequestReader.value(token)
    }
  }
  val short: Endpoint[Short] = Extractor("short", s => Try(s.toShort).toOption)
  val ordInt: Endpoint[OrdInt] = Extractor("ordint", OrdInt.parse)
}
