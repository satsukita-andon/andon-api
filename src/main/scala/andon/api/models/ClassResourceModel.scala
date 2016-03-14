package andon.api.models

import cats.data.Xor
import org.joda.time.DateTime
import scalikejdbc._

import andon.api.errors._
import andon.api.models.generated.{ Class, ClassResource, ClassResourceRevision }
import andon.api.util._

object ClassResourceModel extends ClassResourceModel {
  protected val ClassModel = andon.api.models.ClassModel
}
trait ClassResourceModel {

  protected val ClassModel: ClassModel

  private val c = Class.c
  val cr = ClassResource.cr
  val crr = ClassResourceRevision.crr

  private def revisionOpt(r: SyntaxProvider[ClassResourceRevision])(rs: WrappedResultSet): Option[ClassResourceRevision] =
    rs.shortOpt(r.resultName.id).map(_ => ClassResourceRevision(r)(rs))

  def find(id: Int)(implicit s: DBSession): Option[(ClassResource, ClassResourceRevision)] = {
    withSQL {
      select.from(ClassResource as cr)
        .innerJoin(ClassResourceRevision as crr)
        .on(SQLSyntax.eq(cr.id, crr.resourceId).and
          .eq(cr.latestRevisionNumber, crr.revisionNumber))
        .where
        .eq(cr.id, id)
    }.one(ClassResource(cr))
      .toOne(ClassResourceRevision(crr))
      .map { (resource, revision) => (resource, revision) }
      .single
      .apply()
  }

  def findAll(classId: Short, paging: Paging)(implicit s: DBSession): Seq[(ClassResource, ClassResourceRevision)] = {
    withSQL {
      paging.sql {
        select.from(ClassResource as cr)
          .innerJoin(ClassResourceRevision as crr)
          .on(SQLSyntax.eq(cr.id, crr.resourceId).and
            .eq(cr.latestRevisionNumber, crr.revisionNumber))
          .where
          .eq(cr.classId, classId)
      }
    }.one(ClassResource(cr))
      .toOne(ClassResourceRevision(crr))
      .map { (resource, revision) => (resource, revision) }
      .list
      .apply()
  }
  def findAll(classId: ClassId, paging: Paging)(implicit s: DBSession): Seq[(ClassResource, ClassResourceRevision)] = {
    ClassModel.findId(classId).map(findAll(_, paging)).getOrElse(Seq())
  }

  def count(classId: Short)(implicit s: DBSession): Long = {
    ClassResource.countBy(SQLSyntax.eq(cr.classId, classId))
  }
  def count(classId: ClassId)(implicit s: DBSession): Long = {
    ClassModel.findId(classId).map(count).getOrElse(0L)
  }

  def findRevisions(resourceId: Int, paging: Paging)(implicit s: DBSession): Option[(ClassResource, Seq[ClassResourceRevision])] = {
    withSQL {
      paging.sql {
        select.from(ClassResource as cr)
          .leftJoin(ClassResourceRevision as crr).on(SQLSyntax
            .eq(cr.id, crr.resourceId).and
            .eq(cr.latestRevisionNumber, crr.revisionNumber))
          .where.eq(cr.id, resourceId)
      }
    }.one(ClassResource(cr))
      .toMany(revisionOpt(crr))
      .map((a, rs) => (a, rs))
      .single.apply()
  }

  def countRevisions(resourceId: Int)(implicit s: DBSession): Long = {
    ClassResourceRevision.countBy(SQLSyntax.eq(crr.resourceId, resourceId))
  }

  def findClassId(resourceId: Int)(implicit s: DBSession): Option[ClassId] = {
    withSQL {
      select.from(ClassResource as cr)
        .innerJoin(Class as c).on(cr.classId, c.id)
        .where.eq(cr.id, resourceId)
    }.map(rs => (rs.short(c.times), rs.short(c.times), rs.short(c.`class`)))
      .single.apply()
      .map { case (t, g, c) => ClassId(OrdInt(t), g, c) }
  }

  def create(
    userId: Int, // must be existing user id
    classId: Short, // must be existing class id
    status: PublishingStatus,
    title: String,
    description: String,
    url: String,
    comment: String
  )(implicit s: DBSession): Xor[AndonError, (ClassResource, ClassResourceRevision)] = try {
    val now = DateTime.now
    val cr = ClassResource.create(
      classId = classId,
      latestRevisionNumber = 1,
      status = status.code,
      createdBy = Some(userId),
      updatedBy = Some(userId),
      createdAt = now,
      updatedAt = now
    )
    val rev = ClassResourceRevision.create(
      resourceId = cr.id,
      revisionNumber = 1,
      userId = Some(userId),
      title = title,
      description = description,
      url = url,
      comment = comment,
      createdAt = now
    )
    Xor.right((cr, rev))
  } catch {
    case e: java.sql.SQLException => Xor.left(Incorrect(e.getMessage)) // TODO: check exception type
  }

  def destroy(resourceId: Int)(implicit s: DBSession): Boolean = {
    val n = withSQL {
      delete.from(ClassResource as cr)
        .where.eq(cr.id, resourceId)
    }.update.apply()
    if (n == 0) false else true
  }
}
