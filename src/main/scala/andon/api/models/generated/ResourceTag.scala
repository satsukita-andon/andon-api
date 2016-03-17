package andon.api.models.generated

import scalikejdbc._

case class ResourceTag(
  id: Int,
  resourceId: Int,
  label: String) {

  def save()(implicit session: DBSession): ResourceTag = ResourceTag.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = ResourceTag.destroy(this)(session)

}


object ResourceTag extends SQLSyntaxSupport[ResourceTag] {

  override val tableName = "resource_tags"

  override val columns = Seq("id", "resource_id", "label")

  def apply(rt: SyntaxProvider[ResourceTag])(rs: WrappedResultSet): ResourceTag = apply(rt.resultName)(rs)
  def apply(rt: ResultName[ResourceTag])(rs: WrappedResultSet): ResourceTag = new ResourceTag(
    id = rs.get(rt.id),
    resourceId = rs.get(rt.resourceId),
    label = rs.get(rt.label)
  )

  val rt = ResourceTag.syntax("rt")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[ResourceTag] = {
    withSQL {
      select.from(ResourceTag as rt).where.eq(rt.id, id)
    }.map(ResourceTag(rt.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ResourceTag] = {
    withSQL(select.from(ResourceTag as rt)).map(ResourceTag(rt.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ResourceTag as rt)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ResourceTag] = {
    withSQL {
      select.from(ResourceTag as rt).where.append(where)
    }.map(ResourceTag(rt.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ResourceTag] = {
    withSQL {
      select.from(ResourceTag as rt).where.append(where)
    }.map(ResourceTag(rt.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ResourceTag as rt).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    resourceId: Int,
    label: String)(implicit session: DBSession): ResourceTag = {
    val generatedKey = withSQL {
      insert.into(ResourceTag).columns(
        column.resourceId,
        column.label
      ).values(
        resourceId,
        label
      )
    }.updateAndReturnGeneratedKey.apply()

    ResourceTag(
      id = generatedKey.toInt,
      resourceId = resourceId,
      label = label)
  }

  def batchInsert(entities: Seq[ResourceTag])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'resourceId -> entity.resourceId,
        'label -> entity.label))
        SQL("""insert into resource_tags(
        resource_id,
        label
      ) values (
        {resourceId},
        {label}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: ResourceTag)(implicit session: DBSession): ResourceTag = {
    withSQL {
      update(ResourceTag).set(
        column.id -> entity.id,
        column.resourceId -> entity.resourceId,
        column.label -> entity.label
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ResourceTag)(implicit session: DBSession): Unit = {
    withSQL { delete.from(ResourceTag).where.eq(column.id, entity.id) }.update.apply()
  }

}
