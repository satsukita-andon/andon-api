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
  def orderBy(conv: (String, SQLSyntax)*): RequestReader[Option[Seq[(SQLSyntax, Option[SortOrder])]]] = {
    val p = paramOption("orderby")
    if (conv.isEmpty) { // if `conv` is empty, parameters are ignored
      p.map(_ => None)
    } else {
      val map = conv.toMap
      // TODO: Refactor
      var err = false
      val orders = p.map(_.map(_.split(',').toSeq.map { s =>
        val splitted = s.split(' ')
        if (splitted.length == 0 || splitted.length > 2) { err = true }
        val syntax = splitted.headOption.map { s =>
          map.get(s).getOrElse {
            err = true
            SQLSyntax.empty
          }
        }.getOrElse(SQLSyntax.empty)
        val order = splitted.lift(1).map { s =>
          SortOrder.from(s.toUpperCase).getOrElse {
            err = true
            ASC
          }
        }
        (syntax, order)
      }))

      val message = s"be `field1 order1,field2 order2,...` where field={${conv.map(_._1).mkString("|")}} and order={ASC,DESC}. If `order` is ommited, the order will be ASC."
      orders.should(message)(_ => !err)
    }
  }
  def paging(conv: (String, SQLSyntax)*): RequestReader[Paging] = (
    paramOption("offset").as[Int]
      .should("be non negative")(_.map(_ >= 0).getOrElse(true)) ::
      paramOption("limit").as[Int]
      .should("be non negative")(_.map(_ >= 0).getOrElse(true)) ::
      orderBy(conv: _*)
  ).as[Paging]
  def ordintParamOption(name: String): RequestReader[Option[OrdInt]] = paramOption(name)
    .map(_.map(OrdInt.parse))
    .should("be ??st, ??nd, ??rd, or ??th")(_.map(_.nonEmpty).getOrElse(true))
    .map(_.flatten)
  object short extends Extractor("short", s => Try(s.toShort).toOption)
  object ordint extends Extractor("ordint", OrdInt.parse)
  object classId extends Extractor("class_id", ClassId.parse)
  object fixedContentType extends Extractor("type", FixedContentType.from)
}
