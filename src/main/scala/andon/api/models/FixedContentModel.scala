package andon.api.models

import scalikejdbc._

import andon.api.models.generated._
import andon.api.util._

object FixedContentModel extends FixedContentModel
trait FixedContentModel {

  private val fc = FixedContent.fc
  private val fcr = FixedContentRevision.fcr

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
}
