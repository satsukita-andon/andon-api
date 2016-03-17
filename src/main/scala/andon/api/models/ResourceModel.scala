package andon.api.models

import andon.api.jsons.ResourceCreation
import org.joda.time.DateTime
import scalikejdbc._

import andon.api.models.generated._
import andon.api.util._

object ResourceModel extends ResourceModel {
  val UserModel = andon.api.models.UserModel
}
trait ResourceModel {

  val UserModel: UserModel

  val r = Resource.r
  val rr = ResourceRevision.rr
  val rer = ResourceEditorRel.rer
  val rt = ResourceTag.rt
  private val u = User.u

  def revisionOpt(r: SyntaxProvider[ResourceRevision])(rs: WrappedResultSet): Option[ResourceRevision] =
    rs.shortOpt(r.resultName.id).map(_ => ResourceRevision(r)(rs))

  def resourceTagOpt(t: SyntaxProvider[ResourceTag])(rs: WrappedResultSet): Option[ResourceTag] =
    rs.intOpt(t.resultName.id).map(_ => ResourceTag(t)(rs))

  def editorRelOpt(er: SyntaxProvider[ResourceEditorRel])(rs: WrappedResultSet): Option[ResourceEditorRel] =
    rs.intOpt(er.resultName.id).map(_ => ResourceEditorRel(er)(rs))

  def find(resourceId: Int)(implicit s: DBSession): Option[(Resource, User, Seq[String], ResourceRevision, Option[User])] = {
    // TODO: optimize
    val rot = withSQL {
      select.from(Resource as r)
        .innerJoin(User as u).on(u.id, r.ownerId)
        .leftJoin(ResourceTag as rt).on(r.id, rt.resourceId)
        .where
        .eq(r.id, resourceId)
    }.one(Resource(r))
      .toManies(rs => Some(User(u)(rs)), resourceTagOpt(rt))
      .map((r, os, ts) => (r, os.head, ts.map(_.label))) // head is safe
      .single.apply()

    rot.flatMap { case (r, o, ts) =>
      val ru = withSQL {
        select.from(ResourceRevision as rr)
          .leftJoin(User as u).on(u.id, rr.userId)
          .where
          .eq(rr.resourceId, r.id).and
          .eq(rr.revisionNumber, r.latestRevisionNumber)
      }.one(ResourceRevision(rr))
        .toOne(UserModel.opt(u))
        .map((rr, u) => (rr, u))
        .single.apply()
      ru.map { case (rr, u) => (r, o, ts, rr, u) }
    }
  }

  def findAll(paging: Paging)(implicit s: DBSession): Seq[(Resource, User, Seq[String], ResourceRevision, Option[User])] = {
    // TODO: optimize, N + 1 query
    val rots = withSQL {
      paging.sql {
        select.from(Resource as r)
          .innerJoin(User as u).on(u.id, r.ownerId)
          .leftJoin(ResourceTag as rt).on(rt.resourceId, r.id)
      }
    }.one(Resource(r))
      .toManies(rs => Some(User(u)(rs)), resourceTagOpt(rt))
      .map((r, os, ats) => (r, os.head, ats.map(_.label))) // head is safe
      .list.apply()

    rots.map { case (r, o, ts) =>
      val ru = withSQL {
        select.from(ResourceRevision as rr)
          .leftJoin(User as u).on(u.id, rr.userId)
          .where
          .eq(rr.resourceId, r.id).and
          .eq(rr.revisionNumber, r.latestRevisionNumber)
      }.one(ResourceRevision(rr))
        .toOne(UserModel.opt(u))
        .map((rr, u) => (rr, u))
        .single.apply()
      ru.map { case (rr, u) => (r, o, ts, rr, u) }.get // not cause error
    }
  }

  def findRevisions(
    resourceId: Int, paging: Paging
  )(implicit s: DBSession): Option[(Resource, User, Seq[String], Seq[(ResourceRevision, Option[User])])] = {
    // TODO: optimize
    val rot = withSQL {
      select.from(Resource as r)
        .innerJoin(User as u).on(u.id, r.ownerId)
        .leftJoin(ResourceTag as rt).on(r.id, rt.resourceId)
        .where
        .eq(r.id, resourceId)
    }.one(Resource(r))
      .toManies(rs => Some(User(u)(rs)), resourceTagOpt(rt))
      .map((r, os, rts) => (r, os.head, rts.map(_.label))) // head is safe
      .single.apply()

    rot.map { case (r, o, t) =>
      val rus = withSQL {
        paging.sql {
          select.from(ResourceRevision as rr)
            .leftJoin(User as u).on(u.id, rr.userId)
            .where
            .eq(rr.resourceId, resourceId)
        }
      }.one(ResourceRevision(rr))
        .toOne(UserModel.opt(u))
        .map((rr, u) => (rr, u))
        .list.apply()
      (r, o, t, rus)
    }
  }

  def findMeta(resourceId: Int)(implicit s: DBSession): Option[(PublishingStatus, EditorialRight, Int, Seq[Int])] = {
    withSQL {
      select.from(Resource as r)
        .leftJoin(ResourceEditorRel as rer).on(r.id, rer.resourceId)
        .where
        .eq(r.id, resourceId)
    }.one(Resource(r))
      .toMany(editorRelOpt(rer))
      .map((r: Resource, es: Seq[ResourceEditorRel]) => (
        PublishingStatus.unsafeFrom(r.status),
        EditorialRight.unsafeFrom(r.editorialRight),
        r.ownerId, es.map(_.userId))
      )
      .single.apply()
  }

  def countAll(implicit s: DBSession): Long = {
    Resource.countAll()
  }

  def countRevisions(resourceId: Int)(implicit s: DBSession): Long = {
    ResourceRevision.countBy(SQLSyntax.eq(rr.resourceId, resourceId))
  }

  def create(userId: Int, creation: ResourceCreation)(implicit s: DBSession): (Resource, ResourceRevision) = {
    val now = DateTime.now
    val resource = Resource.create(
      ownerId = userId,
      latestRevisionNumber = 1,
      status = creation.status.toString,
      editorialRight = creation.editorial_right.toString,
      createdBy = Some(userId),
      updatedBy = Some(userId),
      createdAt = now,
      updatedAt = now
    )
    val revision = ResourceRevision.create(
      resourceId = resource.id,
      revisionNumber = 1,
      userId = Some(userId),
      title = creation.title,
      description = creation.description,
      url = creation.url,
      comment = creation.comment,
      createdAt = now
    )
    (resource, revision)
  }

  def updateContent(
    resourceId: Int,
    userId: Int,
    title: String,
    description: String,
    url: String,
    comment: String
  )(implicit s: DBSession): Option[(Resource, ResourceRevision)] = {
    val now = DateTime.now
    Resource.find(resourceId).map { resource =>
      val updated = resource.copy(
        latestRevisionNumber = (resource.latestRevisionNumber + 1).toShort,
        updatedBy = Some(userId),
        updatedAt = now
      ).save()
      val revision = ResourceRevision.create(
        resourceId = resourceId,
        revisionNumber = updated.latestRevisionNumber,
        userId = Some(userId),
        title = title,
        description = description,
        url = url,
        comment = comment,
        createdAt = now
      )
      (updated, revision)
    }
  }

  def updateMeta(
    resourceId: Int,
    userId: Int,
    status: PublishingStatus,
    editorialRight: EditorialRight,
    editorIdSet: Set[Int]
  )(implicit s: DBSession): Option[(Resource, Set[Int])] = {
    val now = DateTime.now
    Resource.find(resourceId).map { resource =>
      // update resource
      val updated = resource.copy(
        status = status.toString,
        editorialRight = editorialRight.toString,
        updatedBy = Some(userId),
        updatedAt = now
      ).save()
      // prepare editor ids
      val currentEditorIdSet = ResourceEditorRel.findAllBy(SQLSyntax.eq(rer.resourceId, resourceId)).map(_.userId).toSet
      val newEditorIdSet = editorIdSet -- currentEditorIdSet
      val removeEditorIdSet = currentEditorIdSet -- editorIdSet
      // insert new editors
      ResourceEditorRel.batchInsert(
        newEditorIdSet.toSeq.map { editorId =>
          ResourceEditorRel(0, resourceId, editorId)
        }
      )
      // remove old editors
      withSQL {
        delete.from(ResourceEditorRel as rer)
          .where.in(rer.userId, removeEditorIdSet.toSeq)
      }.update().apply()
      (updated, editorIdSet)
    }
  }
}
