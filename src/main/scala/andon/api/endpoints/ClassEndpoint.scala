package andon.api.endpoints

import io.finch._
import io.finch.circe._
import io.circe.generic.auto._
import scalikejdbc.DB

import andon.api.jsons.{ ClassArticle, ClassArticleCreation }
import andon.api.models.ClassArticleModel
import andon.api.util._

object ClassEndpoint extends EndpointBase {

  val name = "classes"

  val createArticle: Endpoint[ClassArticle] = post(
    ver / name / ordint("times") / short("grade") / short("class") / "articles" ? body.as[ClassArticleCreation] ? token

  ) { (t: OrdInt, g: Short, c: Short, articleDef: ClassArticleCreation, token: Token) =>
    DB.localTx { implicit s =>
      token.allowedOnly(Right.ClassmateOf(t.raw, g, c)) { user =>
        ClassArticleModel.create(
          userId = user.id,
          times = t,
          grade = g,
          `class` = c,
          status = articleDef.status,
          title = articleDef.title,
          body = articleDef.body,
          comment = articleDef.comment
        ).fold(BadRequest(_), { case (a, r) => Ok(ClassArticle(a, r)) })
      }
    }
  }
}
