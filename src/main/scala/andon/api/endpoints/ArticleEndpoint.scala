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
  def all = create :+: updateContent :+: updateMeta :+: find :+: findAll :+: findRevisions

  def create: Endpoint[DetailedArticle] = post(
    ver / name ? token ? body.as[ArticleCreation]
  ) { (token: Token, creation: ArticleCreation) =>
    DB.localTx { implicit s =>
      token.rejectedOnly(Right.Suspended) { user =>
        creation.validate.toXor.fold(
          { nel => BadRequest(ValidationError(nel)) },
          { creation =>
            val (article, revision) = ArticleModel.create(user.id, creation)
            Ok(DetailedArticle(article, user, Seq(), revision, Some(user)))
          }
        )
      }
    }
  }

  // modify (title, body)
  def updateContent: Endpoint[DetailedArticle] = put(
    ver / name / int ? token ? body.as[ArticleContentModification]
  ) { (articleId: Int, token: Token, modification: ArticleContentModification) =>
    DB.localTx { implicit s =>
      // TODO: refactor and optimize
      def go(user: generated.User): Output[DetailedArticle] = {
        ArticleModel.updateContent(
          articleId = articleId,
          userId = user.id,
          title = modification.title,
          body = modification.body,
          comment = modification.comment
        ).map { _ =>
          ArticleModel.find(articleId).map { case (a, o, ts, r, u) =>
            Ok(DetailedArticle(a, o, ts, r, u))
          }.getOrElse(NotFound(ResourceNotFound()))
        }.getOrElse(NotFound(ResourceNotFound()))
      }
      ArticleModel.findMeta(articleId).map {
        case (_, EditorialRight.All, _, _) => token.withUser(go)
        case (_, EditorialRight.Cohort, ownerId, _) =>
          UserModel.find(ownerId).map { owner =>
            token.allowedOnly(Right.CohortOf(owner.times))(go)
          }.getOrElse(InternalServerError(Unexpected("Owner not found. Please report.")))
        case (_, EditorialRight.Classmate, ownerId, _) =>
          UserModel.find(ownerId).map { owner =>
            token.allowedOnly(ClassId.of(owner).map(Right.ClassmateOf.apply): _*)(go)
          }.getOrElse(InternalServerError(Unexpected("Owner not found. Please report.")))
        case (_, EditorialRight.Selected, ownerId, editorIds) =>
          token.allowedOnly(Right.In((ownerId +: editorIds).toSet))(go)
      }.getOrElse(NotFound(ResourceNotFound()))
    }
  }

  def updateMeta: Endpoint[DetailedArticle] = put(
    ver / name / int / "meta" ? token ? body.as[ArticleMetaModification]
  ) { (articleId: Int, token: Token, modification: ArticleMetaModification) =>
    DB.localTx { implicit s =>
      ArticleModel.findMeta(articleId).map { case (status, right, ownerId, editorIds) =>
        token.allowedOnly(Right.Admin, Right.Is(ownerId)) { user =>
          modification.validate(
            ArticleMetaModification(
              status = status,
              editorial_right = right,
              editor_ids = editorIds
            ), user.admin, ownerId == user.id
          ).toXor.fold(
          { errors =>
            BadRequest(ValidationError(errors))
          }, { modification =>
            ArticleModel.updateMeta(
              articleId = articleId,
              userId = user.id,
              status = modification.status,
              editorialRight = modification.editorial_right,
              editorIdSet = modification.editor_ids.toSet
            )
            ArticleModel.find(articleId).map { case (a, o, ts, r, u) =>
              Ok(DetailedArticle(a, o, ts, r, u))
            }.getOrElse(NotFound(ResourceNotFound()))
          }
          )
        }
      }.getOrElse(NotFound(ResourceNotFound()))
    }
  }

  // all users (not no logged-in user) can update tags
  def updateTags: Endpoint[Unit] = put(
    ver / name / int / "tags" ? token ? body.as[Seq[String]]
  ) { (articleId: Int, token: Token, tags: Seq[String]) =>
    Ok(())
  }

  def find: Endpoint[DetailedArticle] = get(ver / name / int) { articleId: Int =>
    DB.readOnly { implicit s =>
      ArticleModel.find(articleId).map { case (a, o, ts, r, u) =>
        Ok(DetailedArticle(a, o, ts, r, u))
      }.getOrElse(NotFound(ResourceNotFound()))
    }
  }

  def findAll: Endpoint[Items[Article]] = get(
    ver / name ? paging("created_at" -> ArticleModel.a.createdAt, "updated_at" -> ArticleModel.a.updatedAt)
  ){ (p: Paging) =>
    val paging = p.defaultLimit(50).maxLimit(100)
      .defaultOrder((ArticleModel.a.createdAt, DESC))
    DB.readOnly { implicit s =>
      val articles = ArticleModel.findAll(paging).map { case (a, o, ts, r, u) =>
          Article(a, o, ts, r, u)
      }
      val all = ArticleModel.countAll
      Ok(Items(
        count = articles.length.toLong,
        all_count = all,
        items = articles
      ))
    }
  }

  def findRevisions: Endpoint[Items[Article]] = get(
    ver / name / int / "revisions" ? paging()
  ) { (articleId: Int, paging: Paging) =>
      val p = paging.defaultLimit(20).maxLimit(20)
        .defaultOrder((ArticleModel.ar.revisionNumber, DESC))
      DB.readOnly { implicit s =>
        ArticleModel.findRevisions(articleId, p).map { case (a, o, ts, rus) =>
          val all = ArticleModel.countRevisions(articleId)
          Ok(Items(
            count = rus.length.toLong,
            all_count = all,
            items = rus.map { case (r, u) => Article(a, o, ts, r, u) }
          ))
        }.getOrElse(NotFound(ResourceNotFound()))
      }
    }
}
