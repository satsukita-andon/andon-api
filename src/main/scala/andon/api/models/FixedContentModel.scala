package andon.api.models

import scalikejdbc._

import andon.api.models.generated._
import andon.api.util._

object FixedContentModel extends FixedContentModel
trait FixedContentModel {

  val fc = FixedContent.fc
  val fcr = FixedContentRevision.fcr

  def revisionOpt(r: SyntaxProvider[FixedContentRevision])(rs: WrappedResultSet): Option[FixedContentRevision] =
    rs.shortOpt(r.resultName.id).map(_ => FixedContentRevision(r)(rs))

  def findByType(`type`: FixedContentType)(implicit s: DBSession): Option[(FixedContent, FixedContentRevision)] = {
    withSQL {
      select.from(FixedContent as fc)
        .innerJoin(FixedContentRevision as fcr).on(SQLSyntax
          .eq(fcr.contentId, fc.id).and
          .eq(fcr.revisionNumber, fc.latestRevisionNumber))
        .where
        .eq(fc.`type`, `type`.code)
    }.one(FixedContent(fc))
      .toOne(FixedContentRevision(fcr))
      .map((fc, fcr) => (fc, fcr))
      .single.apply()
  }

  def findRevisionsByType(`type`: FixedContentType, paging: Paging)(implicit s: DBSession): Option[(FixedContent, Seq[FixedContentRevision])] = {
    withSQL {
      paging.sql {
        select.from(FixedContent as fc)
          .leftJoin(FixedContentRevision as fcr).on(sqls
            .eq(fcr.contentId, fc.id))
          .where
          .eq(fc.`type`, `type`.code)
      }
    }.one(FixedContent(fc))
      .toMany(revisionOpt(fcr))
      .map((fc, fcrs) => (fc, fcrs))
      .single.apply()
  }

  def countRevisions(contentId: Int)(implicit s: DBSession): Long = {
    FixedContentRevision.countBy(SQLSyntax.eq(fcr.contentId, contentId))
  }
  def countRevisions(`type`: FixedContentType)(implicit s: DBSession): Option[Long] = {
    FixedContent.findBy(SQLSyntax.eq(fc.`type`, `type`.toString)).map { c =>
      countRevisions(c.id)
    }
  }
}
