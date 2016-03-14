package andon.api.models.generated

import scalikejdbc._
import org.joda.time.{DateTime}

case class ClassResource(
  id: Int,
  classId: Short,
  latestRevisionNumber: Short,
  status: String,
  createdBy: Option[Int] = None,
  updatedBy: Option[Int] = None,
  createdAt: DateTime,
  updatedAt: DateTime) {

  def save()(implicit session: DBSession): ClassResource = ClassResource.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = ClassResource.destroy(this)(session)

}


object ClassResource extends SQLSyntaxSupport[ClassResource] {

  override val tableName = "class_resources"

  override val columns = Seq("id", "class_id", "latest_revision_number", "status", "created_by", "updated_by", "created_at", "updated_at")

  def apply(cr: SyntaxProvider[ClassResource])(rs: WrappedResultSet): ClassResource = apply(cr.resultName)(rs)
  def apply(cr: ResultName[ClassResource])(rs: WrappedResultSet): ClassResource = new ClassResource(
    id = rs.get(cr.id),
    classId = rs.get(cr.classId),
    latestRevisionNumber = rs.get(cr.latestRevisionNumber),
    status = rs.get(cr.status),
    createdBy = rs.get(cr.createdBy),
    updatedBy = rs.get(cr.updatedBy),
    createdAt = rs.get(cr.createdAt),
    updatedAt = rs.get(cr.updatedAt)
  )

  val cr = ClassResource.syntax("cr")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[ClassResource] = {
    withSQL {
      select.from(ClassResource as cr).where.eq(cr.id, id)
    }.map(ClassResource(cr.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ClassResource] = {
    withSQL(select.from(ClassResource as cr)).map(ClassResource(cr.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ClassResource as cr)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ClassResource] = {
    withSQL {
      select.from(ClassResource as cr).where.append(where)
    }.map(ClassResource(cr.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ClassResource] = {
    withSQL {
      select.from(ClassResource as cr).where.append(where)
    }.map(ClassResource(cr.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ClassResource as cr).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    classId: Short,
    latestRevisionNumber: Short,
    status: String,
    createdBy: Option[Int] = None,
    updatedBy: Option[Int] = None,
    createdAt: DateTime,
    updatedAt: DateTime)(implicit session: DBSession): ClassResource = {
    val generatedKey = withSQL {
      insert.into(ClassResource).columns(
        column.classId,
        column.latestRevisionNumber,
        column.status,
        column.createdBy,
        column.updatedBy,
        column.createdAt,
        column.updatedAt
      ).values(
        classId,
        latestRevisionNumber,
        status,
        createdBy,
        updatedBy,
        createdAt,
        updatedAt
      )
    }.updateAndReturnGeneratedKey.apply()

    ClassResource(
      id = generatedKey.toInt,
      classId = classId,
      latestRevisionNumber = latestRevisionNumber,
      status = status,
      createdBy = createdBy,
      updatedBy = updatedBy,
      createdAt = createdAt,
      updatedAt = updatedAt)
  }

  def batchInsert(entities: Seq[ClassResource])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'classId -> entity.classId,
        'latestRevisionNumber -> entity.latestRevisionNumber,
        'status -> entity.status,
        'createdBy -> entity.createdBy,
        'updatedBy -> entity.updatedBy,
        'createdAt -> entity.createdAt,
        'updatedAt -> entity.updatedAt))
        SQL("""insert into class_resources(
        class_id,
        latest_revision_number,
        status,
        created_by,
        updated_by,
        created_at,
        updated_at
      ) values (
        {classId},
        {latestRevisionNumber},
        {status},
        {createdBy},
        {updatedBy},
        {createdAt},
        {updatedAt}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: ClassResource)(implicit session: DBSession): ClassResource = {
    withSQL {
      update(ClassResource).set(
        column.id -> entity.id,
        column.classId -> entity.classId,
        column.latestRevisionNumber -> entity.latestRevisionNumber,
        column.status -> entity.status,
        column.createdBy -> entity.createdBy,
        column.updatedBy -> entity.updatedBy,
        column.createdAt -> entity.createdAt,
        column.updatedAt -> entity.updatedAt
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ClassResource)(implicit session: DBSession): Unit = {
    withSQL { delete.from(ClassResource).where.eq(column.id, entity.id) }.update.apply()
  }

}
