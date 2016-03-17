package andon.api.models.generated

import scalikejdbc._

case class ResourceEditorRel(
  id: Int,
  resourceId: Int,
  userId: Int) {

  def save()(implicit session: DBSession): ResourceEditorRel = ResourceEditorRel.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = ResourceEditorRel.destroy(this)(session)

}


object ResourceEditorRel extends SQLSyntaxSupport[ResourceEditorRel] {

  override val tableName = "resource_editor_rel"

  override val columns = Seq("id", "resource_id", "user_id")

  def apply(rer: SyntaxProvider[ResourceEditorRel])(rs: WrappedResultSet): ResourceEditorRel = apply(rer.resultName)(rs)
  def apply(rer: ResultName[ResourceEditorRel])(rs: WrappedResultSet): ResourceEditorRel = new ResourceEditorRel(
    id = rs.get(rer.id),
    resourceId = rs.get(rer.resourceId),
    userId = rs.get(rer.userId)
  )

  val rer = ResourceEditorRel.syntax("rer")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[ResourceEditorRel] = {
    withSQL {
      select.from(ResourceEditorRel as rer).where.eq(rer.id, id)
    }.map(ResourceEditorRel(rer.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ResourceEditorRel] = {
    withSQL(select.from(ResourceEditorRel as rer)).map(ResourceEditorRel(rer.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ResourceEditorRel as rer)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ResourceEditorRel] = {
    withSQL {
      select.from(ResourceEditorRel as rer).where.append(where)
    }.map(ResourceEditorRel(rer.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ResourceEditorRel] = {
    withSQL {
      select.from(ResourceEditorRel as rer).where.append(where)
    }.map(ResourceEditorRel(rer.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ResourceEditorRel as rer).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    resourceId: Int,
    userId: Int)(implicit session: DBSession): ResourceEditorRel = {
    val generatedKey = withSQL {
      insert.into(ResourceEditorRel).columns(
        column.resourceId,
        column.userId
      ).values(
        resourceId,
        userId
      )
    }.updateAndReturnGeneratedKey.apply()

    ResourceEditorRel(
      id = generatedKey.toInt,
      resourceId = resourceId,
      userId = userId)
  }

  def batchInsert(entities: Seq[ResourceEditorRel])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'resourceId -> entity.resourceId,
        'userId -> entity.userId))
        SQL("""insert into resource_editor_rel(
        resource_id,
        user_id
      ) values (
        {resourceId},
        {userId}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: ResourceEditorRel)(implicit session: DBSession): ResourceEditorRel = {
    withSQL {
      update(ResourceEditorRel).set(
        column.id -> entity.id,
        column.resourceId -> entity.resourceId,
        column.userId -> entity.userId
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ResourceEditorRel)(implicit session: DBSession): Unit = {
    withSQL { delete.from(ResourceEditorRel).where.eq(column.id, entity.id) }.update.apply()
  }

}
