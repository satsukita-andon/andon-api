package andon.api.endpoints

import io.finch._
import io.finch.circe._
import io.circe.generic.auto._
import scalikejdbc.DB

import andon.api.errors._
import andon.api.jsons._
import andon.api.models._
import andon.api.util._

object ResourceEndpoint extends ResourceEndpoint {
  val ResourceModel = andon.api.models.ResourceModel
}
trait ResourceEndpoint extends EndpointBase {

  val ResourceModel: ResourceModel

  val name = "resources"
  def all = create :+: updateContent :+: updateMeta :+: find :+: findAll :+: findRevisions

  def create: Endpoint[Resource] = post(
    ver / name ? token ? body.as[ResourceCreation]
  ) { (token: Token, creation: ResourceCreation) =>
    DB.localTx { implicit s =>
      token.rejectedOnly(Right.Suspended) { user =>
        creation.validate.toXor.fold(
          { nel => BadRequest(ValidationError(nel)) },
          { creation =>
            val (resource, revision) = ResourceModel.create(user.id, creation)
            Ok(Resource(resource, user, Seq(), revision, Some(user)))
          }
        )
      }
    }
  }

  // modify (title, description, url)
  def updateContent: Endpoint[Resource] = put(
    ver / name / int ? token ? body.as[ResourceContentModification]
  ) { (resourceId: Int, token: Token, modification: ResourceContentModification) =>
    DB.localTx { implicit s =>
      // TODO: refactor and optimize
      def go(user: generated.User): Output[Resource] = {
        ResourceModel.updateContent(
          resourceId = resourceId,
          userId = user.id,
          title = modification.title,
          description = modification.description,
          url = modification.url,
          comment = modification.comment
        ).map { _ =>
          ResourceModel.find(resourceId).map { case (r, o, ts, rr, u) =>
            Ok(Resource(r, o, ts, rr, u))
          }.getOrElse(NotFound(ResourceNotFound()))
        }.getOrElse(NotFound(ResourceNotFound()))
      }
      ResourceModel.findMeta(resourceId).map {
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

  def updateMeta: Endpoint[Resource] = put(
    ver / name / int / "meta" ? token ? body.as[ResourceMetaModification]
  ) { (resourceId: Int, token: Token, modification: ResourceMetaModification) =>
    DB.localTx { implicit s =>
      ResourceModel.findMeta(resourceId).map { case (status, right, ownerId, editorIds) =>
        token.allowedOnly(Right.Admin, Right.Is(ownerId)) { user =>
          modification.validate(
            ResourceMetaModification(
              status = status,
              editorial_right = right,
              editor_ids = editorIds
            ), user.admin, ownerId == user.id
          ).toXor.fold(
            { errors =>
              BadRequest(ValidationError(errors))
            }, { modification =>
              ResourceModel.updateMeta(
                resourceId = resourceId,
                userId = user.id,
                status = modification.status,
                editorialRight = modification.editorial_right,
                editorIdSet = modification.editor_ids.toSet
              )
              ResourceModel.find(resourceId).map { case (r, o, ts, rr, u) =>
                Ok(Resource(r, o, ts, rr, u))
              }.getOrElse(NotFound(ResourceNotFound()))
            }
          )
        }
      }.getOrElse(NotFound(ResourceNotFound()))
    }
  }

  // all users (not only logged-in user) can update tags
  def updateTags: Endpoint[Unit] = put(
    ver / name / int / "tags" ? token ? body.as[Seq[String]]
  ) { (resourceId: Int, token: Token, tags: Seq[String]) =>
    Ok(())
  }

  def find: Endpoint[Resource] = get(ver / name / int) { resourceId: Int =>
    DB.readOnly { implicit s =>
      ResourceModel.find(resourceId).map { case (r, o, ts, rr, u) =>
        Ok(Resource(r, o, ts, rr, u))
      }.getOrElse(NotFound(ResourceNotFound()))
    }
  }

  def findAll: Endpoint[Items[Resource]] = get(
    ver / name ? paging("created_at" -> ResourceModel.r.createdAt, "updated_at" -> ResourceModel.r.updatedAt)
  ){ (p: Paging) =>
    val paging = p.defaultLimit(50).maxLimit(100)
      .defaultOrder((ResourceModel.r.createdAt, DESC))
    DB.readOnly { implicit s =>
      val resources = ResourceModel.findAll(paging).map { case (r, o, ts, rr, u) =>
        Resource(r, o, ts, rr, u)
      }
      val all = ResourceModel.countAll
      Ok(Items(
        count = resources.length.toLong,
        all_count = all,
        items = resources
      ))
    }
  }

  def findRevisions: Endpoint[Items[Resource]] = get(
    ver / name / int / "revisions" ? paging()
  ) { (resourceId: Int, paging: Paging) =>
    val p = paging.defaultLimit(20).maxLimit(20)
      .defaultOrder((ResourceModel.rr.revisionNumber, DESC))
    DB.readOnly { implicit s =>
      ResourceModel.findRevisions(resourceId, p).map { case (r, o, ts, rus) =>
        val all = ResourceModel.countRevisions(resourceId)
        Ok(Items(
          count = rus.length.toLong,
          all_count = all,
          items = rus.map { case (rr, u) => Resource(r, o, ts, rr, u) }
        ))
      }.getOrElse(NotFound(ResourceNotFound()))
    }
  }
}
