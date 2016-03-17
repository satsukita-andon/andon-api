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
  def all = find :+: findAll :+: findImages :+: findArticles :+: createArticle :+: findResources :+: findReviews

  def find: Endpoint[Class] = get(ver / name / classId) { (classId: ClassId) =>
    DB.readOnly { implicit s =>
      ClassModel.findWithPrizesAndTags(classId).map { case (clazz, prizes, tags) =>
        Ok(Class(`class` = clazz, prizes = prizes, tags = tags))
      }.getOrElse(NotFound(ResourceNotFound(s"${classId} is not found.")))
    }
  }

  def findAll: Endpoint[Items[Class]] = get(
    ver / name ? ordintParamOption("times") ? paramOption("grade").as[Short] ? paramOption("class").as[Short]
      ? paging("times" -> ClassModel.c.times, "grade" -> ClassModel.c.grade, "class" -> ClassModel.c.`class`)
  ) { (times: Option[OrdInt], grade: Option[Short], `class`: Option[Short], p: Paging) =>
    val paging = p.defaultLimit(30).maxLimit(100)
      .defaultOrder(ClassModel.c.times -> DESC, ClassModel.c.grade -> ASC, ClassModel.c.`class` -> ASC)
    DB.readOnly { implicit s =>
      val classes = ClassModel.findAllWithPrizesAndTags(times, grade, `class`, paging).map { case (clazz, prizes, tags) =>
        Class(`class` = clazz, prizes = prizes, tags = tags)
      }
      val all = ClassModel.countBy(times, grade, `class`)
      Ok(Items(
        count = classes.length.toLong,
        all_count = all,
        items = classes
      ))
    }
  }

  def findImages: Endpoint[Items[ClassImage]] = get(
    ver / name / classId / "images" ? paging()
  ) { (classId: ClassId, paging: Paging) =>
    DB.readOnly { implicit s =>
      ClassModel.findId(classId).map { id =>
        val p = paging.defaultLimit(50).maxLimit(100)
          .minimumOrder(ClassImageModel.ci.id -> ASC)
          .defaultOrder(ClassImageModel.ci.createdAt -> ASC)
        val images = ClassImageModel.findAll(id, p).map { case (i, u) =>
          ClassImage.apply(i, u)
        }
        val count = ClassImageModel.count(id)
        Ok(Items(all_count = count, count = images.length.toLong, items = images))
      }.getOrElse(NotFound(ResourceNotFound(s"${classId} is not found.")))
    }
  }

  def findArticles: Endpoint[Items[ClassArticle]] = get(
    ver / name / classId / "articles" ? paging()
  ) { (classId: ClassId, paging: Paging) =>
    DB.readOnly { implicit s =>
      ClassModel.findId(classId).map { id =>
        val p = paging
          .minimumOrder(ClassArticleModel.ca.id -> ASC)
          .defaultOrder(ClassArticleModel.ca.createdAt -> DESC)
        val articles = ClassArticleModel.findAll(id, p).map { case (a, r, c, u) =>
          ClassArticle(a, r, c, u)
        }
        val count = ClassArticleModel.count(id)
        Ok(Items(all_count = count, count = articles.length.toLong, items = articles))
      }.getOrElse(NotFound(ResourceNotFound(s"${classId} is not found.")))
    }
  }

  def createArticle: Endpoint[DetailedClassArticle] = post(
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

  def findResources: Endpoint[Items[ClassResource]] = get(
    ver / name / classId / "resources" ? paging()
  ) { (classId: ClassId, paging: Paging) =>
    DB.readOnly { implicit s =>
      ClassModel.findId(classId).map { id =>
        val p = paging
          .minimumOrder(ClassResourceModel.cr.id -> ASC)
          .defaultOrder(ClassResourceModel.cr.createdAt -> DESC)
        val resources = ClassResourceModel.findAll(id, p).map { case (r, rr) =>
          ClassResource(r, rr)
        }
        val count = ClassResourceModel.count(id)
        Ok(Items(all_count = count, count = resources.length.toLong, items = resources))
      }.getOrElse(NotFound(ResourceNotFound(s"${classId} is not found.")))
    }
  }

  def findReviews: Endpoint[Items[ClassReview]] = get(
    ver / name / classId / "reviews" ? paging()
  ) { (classId: ClassId, paging: Paging) =>
    DB.readOnly { implicit s =>
      ClassModel.findId(classId).map { id =>
        val p = paging.defaultOrder(ClassReviewModel.cr.createdAt -> ASC)
        val reviews = ClassReviewModel.findAll(id, p).map { case (cr, u) =>
          ClassReview(cr, u)
        }
        val count = ClassReviewModel.count(id)
        Ok(Items(all_count = count, count = reviews.length.toLong, items = reviews))
      }.getOrElse(NotFound(ResourceNotFound(s"${classId} is not found.")))
    }
  }
}
