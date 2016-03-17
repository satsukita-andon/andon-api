package andon.api.models.generated

import scalikejdbc._
import org.joda.time.{DateTime}

case class Resource(
  id: Int,
  latestRevisionNumber: Short,
  status: String,
  ownerId: Int,
  editorialRight: String,
  createdBy: Option[Int] = None,
  updatedBy: Option[Int] = None,
  createdAt: DateTime,
  updatedAt: DateTime) {

  def save()(implicit session: DBSession): Resource = Resource.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = Resource.destroy(this)(session)

}


object Resource extends SQLSyntaxSupport[Resource] {

  override val tableName = "resources"

  override val columns = Seq("id", "latest_revision_number", "status", "owner_id", "editorial_right", "created_by", "updated_by", "created_at", "updated_at")

  def apply(r: SyntaxProvider[Resource])(rs: WrappedResultSet): Resource = apply(r.resultName)(rs)
  def apply(r: ResultName[Resource])(rs: WrappedResultSet): Resource = new Resource(
    id = rs.get(r.id),
    latestRevisionNumber = rs.get(r.latestRevisionNumber),
    status = rs.get(r.status),
    ownerId = rs.get(r.ownerId),
    editorialRight = rs.get(r.editorialRight),
    createdBy = rs.get(r.createdBy),
    updatedBy = rs.get(r.updatedBy),
    createdAt = rs.get(r.createdAt),
    updatedAt = rs.get(r.updatedAt)
  )

  val r = Resource.syntax("r")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[Resource] = {
    withSQL {
      select.from(Resource as r).where.eq(r.id, id)
    }.map(Resource(r.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[Resource] = {
    withSQL(select.from(Resource as r)).map(Resource(r.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(Resource as r)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[Resource] = {
    withSQL {
      select.from(Resource as r).where.append(where)
    }.map(Resource(r.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[Resource] = {
    withSQL {
      select.from(Resource as r).where.append(where)
    }.map(Resource(r.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(Resource as r).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    latestRevisionNumber: Short,
    status: String,
    ownerId: Int,
    editorialRight: String,
    createdBy: Option[Int] = None,
    updatedBy: Option[Int] = None,
    createdAt: DateTime,
    updatedAt: DateTime)(implicit session: DBSession): Resource = {
    val generatedKey = withSQL {
      insert.into(Resource).columns(
        column.latestRevisionNumber,
        column.status,
        column.ownerId,
        column.editorialRight,
        column.createdBy,
        column.updatedBy,
        column.createdAt,
        column.updatedAt
      ).values(
        latestRevisionNumber,
        status,
        ownerId,
        editorialRight,
        createdBy,
        updatedBy,
        createdAt,
        updatedAt
      )
    }.updateAndReturnGeneratedKey.apply()

    Resource(
      id = generatedKey.toInt,
      latestRevisionNumber = latestRevisionNumber,
      status = status,
      ownerId = ownerId,
      editorialRight = editorialRight,
      createdBy = createdBy,
      updatedBy = updatedBy,
      createdAt = createdAt,
      updatedAt = updatedAt)
  }

  def batchInsert(entities: Seq[Resource])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'latestRevisionNumber -> entity.latestRevisionNumber,
        'status -> entity.status,
        'ownerId -> entity.ownerId,
        'editorialRight -> entity.editorialRight,
        'createdBy -> entity.createdBy,
        'updatedBy -> entity.updatedBy,
        'createdAt -> entity.createdAt,
        'updatedAt -> entity.updatedAt))
        SQL("""insert into resources(
        latest_revision_number,
        status,
        owner_id,
        editorial_right,
        created_by,
        updated_by,
        created_at,
        updated_at
      ) values (
        {latestRevisionNumber},
        {status},
        {ownerId},
        {editorialRight},
        {createdBy},
        {updatedBy},
        {createdAt},
        {updatedAt}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: Resource)(implicit session: DBSession): Resource = {
    withSQL {
      update(Resource).set(
        column.id -> entity.id,
        column.latestRevisionNumber -> entity.latestRevisionNumber,
        column.status -> entity.status,
        column.ownerId -> entity.ownerId,
        column.editorialRight -> entity.editorialRight,
        column.createdBy -> entity.createdBy,
        column.updatedBy -> entity.updatedBy,
        column.createdAt -> entity.createdAt,
        column.updatedAt -> entity.updatedAt
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Resource)(implicit session: DBSession): Unit = {
    withSQL { delete.from(Resource).where.eq(column.id, entity.id) }.update.apply()
  }

}
