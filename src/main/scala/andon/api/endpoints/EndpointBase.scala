package andon.api.endpoints

import cats.data.Xor
import scalikejdbc.interpolation.SQLSyntax

import scala.util.Try
import io.finch._

import andon.api.errors._
import andon.api.util._

trait EndpointBase {
  val ver = "dev"
  val name: String
  // TODO: I want to make `all` an abstract method of `EndpointBase`,
  //       but I don't know what type parameter should I pass to `Endpoint`.
  // def all[C <: Coproduct]: Endpoint[C]

  // must be handle exception to cast status-code to 401 Unauthorized
  val token: RequestReader[Token] = headerOption("Authorization").flatMap { header =>
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
  def orderBy(possibles: String*): RequestReader[Option[Xor[String, SQLSyntax]]] = {
    val p = paramOption("orderby").as[String]
    if (possibles.isEmpty) {
      p.map(_ => None)
    } else {
      p.should(s"be ${possibles.mkString(" or ")}")(_.map(possibles.contains).getOrElse(true))
        .map(_.map(Xor.left))
    }
  }
  val order: RequestReader[Option[SortOrder]] = paramOption("order").as[String]
    .map(_.map(_.toUpperCase))
    .should("be ASC or DESC")(_.map(SortOrder.images.contains(_)).getOrElse(true))
    .map(_.flatMap(SortOrder.from))
  def paging(possibles: String*): RequestReader[Paging] = (
    paramOption("offset").as[Int]
      .should("be non negative")(_.map(_ >= 0).getOrElse(true)) ::
      paramOption("limit").as[Int]
      .should("be non negative")(_.map(_ >= 0).getOrElse(true)) ::
      order ::
      orderBy(possibles: _*)
  ).as[Paging]
  object short extends Extractor("short", s => Try(s.toShort).toOption)
  object ordint extends Extractor("ordint", OrdInt.parse)
  object classId extends Extractor("class_id", ClassId.parse)
  object fixedContentType extends Extractor("type", FixedContentType.from)
}
