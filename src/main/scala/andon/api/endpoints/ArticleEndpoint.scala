package andon.api.endpoints

import io.finch._
import io.finch.circe._
import io.circe.generic.auto._
import scalikejdbc.DB

import andon.api.errors._
import andon.api.jsons._
import andon.api.models._
import andon.api.util._

object ArticleEndpoint extends ArticleEndpoint {
  val ArticleModel = andon.api.models.ArticleModel
}
trait ArticleEndpoint extends EndpointBase {

  val ArticleModel: ArticleModel

  val name = "articles"
  def all = create :+: find :+: findRevisions

  val create: Endpoint[DetailedArticle] = post(
    ver / name ? token ? body.as[ArticleCreation]
  ) { (token: Token, creation: ArticleCreation) =>
    DB.localTx { implicit s =>
      token.rejectedOnly(Right.Suspended) { user =>
        creation.validate.toXor.fold(
          { nel => BadRequest(ValidationError(nel)) },
          { creation =>
            val (article, revision) = ArticleModel.create(user.id, creation)
            Ok(DetailedArticle(article, user, revision, Some(user)))
          }
        )
      }
    }
  }

  val find: Endpoint[DetailedArticle] = get(ver / name / int) { articleId: Int =>
    DB.readOnly { implicit s =>
      ArticleModel.find(articleId).map { case (a, o, r, u) =>
        Ok(DetailedArticle(a, o, r, u))
      }.getOrElse(NotFound(ResourceNotFound()))
    }
  }

  val findRevisions: Endpoint[Items[Article]] = get(
    ver / name / int / "revisions" ? paging
  ) { (articleId: Int, paging: Paging) =>
      val p = paging.defaultLimit(20).maxLimit(20).defaultOrder(DESC)
      DB.readOnly { implicit s =>
        ArticleModel.findRevisions(articleId, p).map { case (a, o, rus) =>
          val all = ArticleModel.countRevisions(articleId)
          Ok(Items(
            count = rus.length.toLong,
            all_count = all,
            items = rus.map { case (r, u) => Article(a, o, r, u) }
          ))
        }.getOrElse(NotFound(ResourceNotFound()))
      }
    }
}
