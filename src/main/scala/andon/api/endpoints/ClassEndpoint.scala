package andon.api.endpoints

import io.finch._
import io.finch.circe._
import io.circe.generic.auto._
import scalikejdbc.DB

import andon.api.errors._
import andon.api.jsons._
import andon.api.models._
import andon.api.util._

object ClassEndpoint extends ClassEndpoint {
  protected val ClassModel = andon.api.models.ClassModel
  protected val ClassImageModel = andon.api.models.ClassImageModel
  protected val ClassArticleModel = andon.api.models.ClassArticleModel
}
trait ClassEndpoint extends EndpointBase {

  protected val ClassModel: ClassModel
  protected val ClassImageModel: ClassImageModel
  protected val ClassArticleModel: ClassArticleModel

  val name = "classes"
  def all = find :+: findImages :+: findArticles :+: createArticle

  val find: Endpoint[Class] = get(ver / name / classId) { (classId: ClassId) =>
    DB.readOnly { implicit s =>
      ClassModel.findWithPrizesAndTags(classId).map { case (clazz, prizes, tags) =>
        Ok(Class(`class` = clazz, prizes = prizes, tags = tags))
      }.getOrElse(NotFound(ResourceNotFound(s"${classId} is not found.")))
    }
  }

  val findImages: Endpoint[Items[ClassImage]] = get(
    ver / name / classId / "images" ? paging()
  ) { (classId: ClassId, paging: Paging) =>
    DB.readOnly { implicit s =>
      ClassModel.findId(classId).map { id =>
        val p = paging.defaultLimit(50).maxLimit(100)
          .defaultOrder(ASC).defaultOrderBy(ClassImageModel.ci.createdAt)
        val images = ClassImageModel.findAll(id, p).map { case (i, u) =>
          ClassImage.apply(i, u)
        }
        val count = ClassImageModel.count(id)
        Ok(Items(all_count = count, count = images.length.toLong, items = images))
      }.getOrElse(NotFound(ResourceNotFound(s"${classId} is not found.")))
    }
  }

  val findArticles: Endpoint[Items[ClassArticle]] = get(
    ver / name / classId / "articles" ? paging()
  ) { (classId: ClassId, paging: Paging) =>
    DB.readOnly { implicit s =>
      ClassModel.findId(classId).map { id =>
        val p = paging.defaultOrder(DESC).defaultOrderBy(ClassArticleModel.ca.createdAt)
        val articles = ClassArticleModel.findAll(id, p).map { case (a, r) =>
          ClassArticle(a, r)
        }
        val count = ClassArticleModel.count(id)
        Ok(Items(all_count = count, count = articles.length.toLong, items = articles))
      }.getOrElse(NotFound(ResourceNotFound(s"${classId} is not found.")))
    }
  }

  val createArticle: Endpoint[DetailedClassArticle] = post(
    ver / name / classId / "articles" ? token ? body.as[ClassArticleCreation]
  ) { (classId: ClassId, token: Token, articleDef: ClassArticleCreation) =>
    DB.localTx { implicit s =>
      token.allowedOnly(Right.ClassmateOf(classId)) { user =>
        articleDef.validate.toXor.fold(
          { nel => BadRequest(ValidationError(nel)) },
          { articleDef =>
            ClassModel.findWithPrizesAndTags(classId).map { case (clazz, prizes, tags) =>
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
                  tags = tags,
                  article = a,
                  revision = r,
                  createdBy = Some(user),
                  updatedBy = Some(user)
                ))
              })
            }.getOrElse(
              NotFound(ResourceNotFound(s"${classId} is not found."))
            )
          }
        )
      }
    }
  }
}
