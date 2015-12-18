package andon.api.endpoints

import io.finch._
import io.finch.circe._
import io.circe.generic.auto._
import scalikejdbc.DB

import andon.api.errors._
import andon.api.jsons.{ DetailedClassArticle, ClassArticleCreation }
import andon.api.models._
import andon.api.util._

object ClassEndpoint extends EndpointBase {

  val name = "classes"

  val createArticle: Endpoint[DetailedClassArticle] = post(
    ver / name / ordint("times") / short("grade") / short("class") / "articles" ? body.as[ClassArticleCreation] ? token

  ) { (t: OrdInt, g: Short, c: Short, articleDef: ClassArticleCreation, token: Token) =>
    DB.localTx { implicit s =>
      token.allowedOnly(Right.ClassmateOf(t.raw, g, c)) { user =>
        ClassModel.findWithPrizes(t, g, c).map { case (clazz, prizes) =>
          ClassArticleModel.create(
            userId = user.id,
            classId = clazz.id,
            status = articleDef.status,
            title = articleDef.title,
            body = articleDef.body,
            comment = articleDef.comment
          ).fold(BadRequest(_), { case (a, r) =>
            Ok(DetailedClassArticle(
              `class` = clazz,
              prizes = prizes,
              article = a,
              revision = r,
              createdBy = Some(user),
              updatedBy = Some(user)
            ))
          })
        }.getOrElse(
          NotFound(ResourceNotFound(s"${t}${g}-${c} is not found."))
        )
      }
    }
  }
}
