package andon.api.endpoints

import io.finch._
import scalikejdbc.DB

import andon.api.errors._
import andon.api.jsons._
import andon.api.models._
import andon.api.util._

object ClassArticleEndpoint extends ClassArticleEndpoint {
  val ClassArticleModel = andon.api.models.ClassArticleModel
}
trait ClassArticleEndpoint extends EndpointBase {

  val ClassArticleModel: ClassArticleModel

  val name = "class-articles"
  def all = findRevisions :+: destroy

  def findRevisions: Endpoint[Items[ClassArticle]] = get(
    ver / name / int / "revisions" ? paging()
  ) { (articleId: Int, paging: Paging) =>
      DB.readOnly { implicit s =>
        ClassArticleModel.findRevisions(articleId, paging).map { case (a, rs) =>
          val all = ClassArticleModel.countRevisions(articleId)
          Ok(Items(
            all_count = all,
            count = rs.length.toLong,
            items = rs.map(r => ClassArticle(a, r))
          ))
        }.getOrElse(NotFound(ResourceNotFound()))
      }
    }

  def destroy: Endpoint[Unit] = delete(ver / name / int ? token) { (articleId: Int, token: Token) =>
    DB.localTx { implicit s =>
      ClassArticleModel.findClassId(articleId).map { classId =>
        token.allowedOnly(Right.ClassmateOf(classId)) { _ =>
          if (ClassArticleModel.destroy(articleId)) {
            NoContent(())
          } else {
            NotFound(ResourceNotFound(s"article ${classId}/${articleId} is not found."))
          }
        }
      }.getOrElse(NotFound(ResourceNotFound(s"article ${articleId} is not found")))
    }
  }
}
