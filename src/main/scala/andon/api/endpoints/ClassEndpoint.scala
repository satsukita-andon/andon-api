package andon.api.endpoints

import io.finch._
import io.finch.circe._
import io.circe.generic.auto._
import scalikejdbc.DB

import andon.api.errors._
import andon.api.jsons._
import andon.api.models._
import andon.api.util._

object ClassEndpoint extends EndpointBase {

  val name = "classes"

  val find: Endpoint[Class] = get(ver / name / classId) { (classId: ClassId) =>
    DB.readOnly { implicit s =>
      ClassModel.findWithPrizesAndTags(classId).map { case (clazz, prizes, tags) =>
        Ok(Class(`class` = clazz, prizes = prizes, tags = tags))
      }.getOrElse(NotFound(ResourceNotFound(s"${classId} is not found.")))
    }
  }

  val findImages: Endpoint[Items[ClassImage]] = get(
    ver / name / classId / "images" ? paging
  ) { (classId: ClassId, paging: Paging) =>
    DB.readOnly { implicit s =>
      ClassModel.findId(classId).map { id =>
        val p = paging.defaultLimit(50)
        val images = ClassImageModel.findAll(id, p).map { case (i, u) =>
          ClassImage.apply(i, u)
        }
        val count = ClassImageModel.count(id)
        Ok(Items(all_count = count, count = images.length.toLong, items = images))
      }.getOrElse(NotFound(ResourceNotFound(s"${classId} is not found.")))
    }
  }

  val findArticles: Endpoint[Items[ClassArticle]] = get(
    ver / name / classId / "articles" ? paging
  ) { (classId: ClassId, p: Paging) =>
    DB.readOnly { implicit s =>
      ClassModel.findId(classId).map { id =>
        val articles = ClassArticleModel.findAll(id, p).map { case (a, r) =>
          ClassArticle(a, r)
        }
        val count = ClassArticleModel.count(id)
        Ok(Items(all_count = count, count = articles.length.toLong, items = articles))
      }.getOrElse(NotFound(ResourceNotFound(s"${classId} is not found.")))
    }
  }

  val createArticle: Endpoint[DetailedClassArticle] = post(
    ver / name / classId / "articles" ? body.as[ClassArticleCreation] ? token
  ) { (classId: ClassId, articleDef: ClassArticleCreation, token: Token) =>
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

  val all = find :+: findImages :+: findArticles :+: createArticle
}
