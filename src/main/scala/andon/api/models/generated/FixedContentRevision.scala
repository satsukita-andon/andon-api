package andon.api.models.generated

import scalikejdbc._
import org.joda.time.{DateTime}

case class FixedContentRevision(
  id: Int,
  contentId: Int,
  revisionNumber: Short,
  userId: Option[Int] = None,
  body: String,
  comment: String,
  createdAt: DateTime) {

  def save()(implicit session: DBSession): FixedContentRevision = FixedContentRevision.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = FixedContentRevision.destroy(this)(session)

}


object FixedContentRevision extends SQLSyntaxSupport[FixedContentRevision] {

  override val tableName = "fixed_content_revisions"

  override val columns = Seq("id", "content_id", "revision_number", "user_id", "body", "comment", "created_at")

  def apply(fcr: SyntaxProvider[FixedContentRevision])(rs: WrappedResultSet): FixedContentRevision = apply(fcr.resultName)(rs)
  def apply(fcr: ResultName[FixedContentRevision])(rs: WrappedResultSet): FixedContentRevision = new FixedContentRevision(
    id = rs.get(fcr.id),
    contentId = rs.get(fcr.contentId),
    revisionNumber = rs.get(fcr.revisionNumber),
    userId = rs.get(fcr.userId),
    body = rs.get(fcr.body),
    comment = rs.get(fcr.comment),
    createdAt = rs.get(fcr.createdAt)
  )

  val fcr = FixedContentRevision.syntax("fcr")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[FixedContentRevision] = {
    withSQL {
      select.from(FixedContentRevision as fcr).where.eq(fcr.id, id)
    }.map(FixedContentRevision(fcr.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[FixedContentRevision] = {
    withSQL(select.from(FixedContentRevision as fcr)).map(FixedContentRevision(fcr.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(FixedContentRevision as fcr)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[FixedContentRevision] = {
    withSQL {
      select.from(FixedContentRevision as fcr).where.append(where)
    }.map(FixedContentRevision(fcr.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[FixedContentRevision] = {
    withSQL {
      select.from(FixedContentRevision as fcr).where.append(where)
    }.map(FixedContentRevision(fcr.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(FixedContentRevision as fcr).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    contentId: Int,
    revisionNumber: Short,
    userId: Option[Int] = None,
    body: String,
    comment: String,
    createdAt: DateTime)(implicit session: DBSession): FixedContentRevision = {
    val generatedKey = withSQL {
      insert.into(FixedContentRevision).columns(
        column.contentId,
        column.revisionNumber,
        column.userId,
        column.body,
        column.comment,
        column.createdAt
      ).values(
        contentId,
        revisionNumber,
        userId,
        body,
        comment,
        createdAt
      )
    }.updateAndReturnGeneratedKey.apply()

    FixedContentRevision(
      id = generatedKey.toInt,
      contentId = contentId,
      revisionNumber = revisionNumber,
      userId = userId,
      body = body,
      comment = comment,
      createdAt = createdAt)
  }

  def batchInsert(entities: Seq[FixedContentRevision])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'contentId -> entity.contentId,
        'revisionNumber -> entity.revisionNumber,
        'userId -> entity.userId,
        'body -> entity.body,
        'comment -> entity.comment,
        'createdAt -> entity.createdAt))
        SQL("""insert into fixed_content_revisions(
        content_id,
        revision_number,
        user_id,
        body,
        comment,
        created_at
      ) values (
        {contentId},
        {revisionNumber},
        {userId},
        {body},
        {comment},
        {createdAt}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: FixedContentRevision)(implicit session: DBSession): FixedContentRevision = {
    withSQL {
      update(FixedContentRevision).set(
        column.id -> entity.id,
        column.contentId -> entity.contentId,
        column.revisionNumber -> entity.revisionNumber,
        column.userId -> entity.userId,
        column.body -> entity.body,
        column.comment -> entity.comment,
        column.createdAt -> entity.createdAt
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: FixedContentRevision)(implicit session: DBSession): Unit = {
    withSQL { delete.from(FixedContentRevision).where.eq(column.id, entity.id) }.update.apply()
  }

}
