package andon.api.endpoints

import io.finch._
import scalikejdbc.DB

import andon.api.errors._
import andon.api.jsons._
import andon.api.models._
import andon.api.util._

object ClassResourceEndpoint extends ClassResourceEndpoint {
  val ClassResourceModel = andon.api.models.ClassResourceModel
  val ClassModel = andon.api.models.ClassModel
  val UserModel = andon.api.models.UserModel
}
trait ClassResourceEndpoint extends EndpointBase {

  val ClassResourceModel: ClassResourceModel
  val ClassModel: ClassModel
  val UserModel: UserModel

  val name = "class-resources"
  def all = find :+: findRevisions :+: destroy

  def find: Endpoint[DetailedClassResource] = get(ver / name / int) { resourceId: Int =>
    DB.readOnly { implicit s =>
      (for {
        (r, rr) <- ClassResourceModel.find(resourceId)
        (c, ps, ts) <- ClassModel.findWithPrizesAndTags(r.classId)
      } yield {
        val cu = r.createdBy.flatMap(UserModel.find)
        val uu = rr.userId.flatMap(UserModel.find) // TODO: rr.userId?
        Ok(DetailedClassResource(c, ps, ts, r, rr, cu, uu))
      }).getOrElse(NotFound(ResourceNotFound()))
    }
  }

  def findRevisions: Endpoint[Items[ClassResource]] = get(
    ver / name / int / "revisions" ? paging()
  ) { (resourceId: Int, paging: Paging) =>
      DB.readOnly { implicit s =>
        ClassResourceModel.findRevisions(resourceId, paging).map { case (a, rs) =>
          val all = ClassResourceModel.countRevisions(resourceId)
          Ok(Items(
            all_count = all,
            count = rs.length.toLong,
            items = rs.map(r => ClassResource(a, r))
          ))
        }.getOrElse(NotFound(ResourceNotFound()))
      }
    }

  def destroy: Endpoint[Unit] = delete(ver / name / int ? token) { (resourceId: Int, token: Token) =>
    DB.localTx { implicit s =>
      ClassResourceModel.findClassId(resourceId).map { classId =>
        token.allowedOnly(Right.ClassmateOf(classId)) { _ =>
          if (ClassResourceModel.destroy(resourceId)) {
            NoContent[Unit]
          } else {
            NotFound(ResourceNotFound(s"resource ${classId}/${resourceId} is not found."))
          }
        }
      }.getOrElse(NotFound(ResourceNotFound(s"resource ${resourceId} is not found")))
    }
  }
}
