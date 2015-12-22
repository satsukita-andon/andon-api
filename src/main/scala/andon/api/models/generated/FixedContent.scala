package andon.api.models.generated

import scalikejdbc._
import org.joda.time.{DateTime}

case class FixedContent(
  id: Int,
  `type`: String,
  latestRevisionNumber: Short,
  updatedBy: Option[Int] = None,
  updatedAt: DateTime) {

  def save()(implicit session: DBSession): FixedContent = FixedContent.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = FixedContent.destroy(this)(session)

}


object FixedContent extends SQLSyntaxSupport[FixedContent] {

  override val tableName = "fixed_contents"

  override val columns = Seq("id", "type", "latest_revision_number", "updated_by", "updated_at")

  def apply(fc: SyntaxProvider[FixedContent])(rs: WrappedResultSet): FixedContent = apply(fc.resultName)(rs)
  def apply(fc: ResultName[FixedContent])(rs: WrappedResultSet): FixedContent = new FixedContent(
    id = rs.get(fc.id),
    `type` = rs.get(fc.`type`),
    latestRevisionNumber = rs.get(fc.latestRevisionNumber),
    updatedBy = rs.get(fc.updatedBy),
    updatedAt = rs.get(fc.updatedAt)
  )

  val fc = FixedContent.syntax("fc")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[FixedContent] = {
    withSQL {
      select.from(FixedContent as fc).where.eq(fc.id, id)
    }.map(FixedContent(fc.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[FixedContent] = {
    withSQL(select.from(FixedContent as fc)).map(FixedContent(fc.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(FixedContent as fc)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[FixedContent] = {
    withSQL {
      select.from(FixedContent as fc).where.append(where)
    }.map(FixedContent(fc.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[FixedContent] = {
    withSQL {
      select.from(FixedContent as fc).where.append(where)
    }.map(FixedContent(fc.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(FixedContent as fc).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    `type`: String,
    latestRevisionNumber: Short,
    updatedBy: Option[Int] = None,
    updatedAt: DateTime)(implicit session: DBSession): FixedContent = {
    val generatedKey = withSQL {
      insert.into(FixedContent).columns(
        column.`type`,
        column.latestRevisionNumber,
        column.updatedBy,
        column.updatedAt
      ).values(
        `type`,
        latestRevisionNumber,
        updatedBy,
        updatedAt
      )
    }.updateAndReturnGeneratedKey.apply()

    FixedContent(
      id = generatedKey.toInt,
      `type` = `type`,
      latestRevisionNumber = latestRevisionNumber,
      updatedBy = updatedBy,
      updatedAt = updatedAt)
  }

  def batchInsert(entities: Seq[FixedContent])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'type -> entity.`type`,
        'latestRevisionNumber -> entity.latestRevisionNumber,
        'updatedBy -> entity.updatedBy,
        'updatedAt -> entity.updatedAt))
        SQL("""insert into fixed_contents(
        type,
        latest_revision_number,
        updated_by,
        updated_at
      ) values (
        {type},
        {latestRevisionNumber},
        {updatedBy},
        {updatedAt}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: FixedContent)(implicit session: DBSession): FixedContent = {
    withSQL {
      update(FixedContent).set(
        column.id -> entity.id,
        column.`type` -> entity.`type`,
        column.latestRevisionNumber -> entity.latestRevisionNumber,
        column.updatedBy -> entity.updatedBy,
        column.updatedAt -> entity.updatedAt
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: FixedContent)(implicit session: DBSession): Unit = {
    withSQL { delete.from(FixedContent).where.eq(column.id, entity.id) }.update.apply()
  }

}
