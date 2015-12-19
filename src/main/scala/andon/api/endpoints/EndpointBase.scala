package andon.api.endpoints

import scala.util.Try
import io.finch._

import andon.api.errors._
import andon.api.util._

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
  val order: RequestReader[Option[SortOrder]] = paramOption("order").as[String]
    .should("be ASC or DESC")(_.map(SortOrder.images.contains(_)).getOrElse(true))
    .map(_.flatMap(SortOrder.from))
  val paging: RequestReader[Paging] = (
    paramOption("offset").as[Int]
      .should("be non negative")(_.map(_ >= 0).getOrElse(true)) ::
      paramOption("limit").as[Int]
      .should("be non negative")(_.map(_ >= 0).getOrElse(true)) ::
      order
  ).as[Paging]
  object short extends Extractor("short", s => Try(s.toShort).toOption)
  object ordint extends Extractor("ordint", OrdInt.parse)
  object classId extends Extractor("class_id", ClassId.parse)
}
