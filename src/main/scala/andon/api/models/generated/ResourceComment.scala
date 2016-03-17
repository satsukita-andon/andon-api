package andon.api.models.generated

import scalikejdbc._
import org.joda.time.{DateTime}

case class ResourceComment(
  id: Int,
  resourceId: Int,
  userId: Option[Int] = None,
  name: Option[String] = None,
  password: Option[String] = None,
  body: String,
  createdAt: DateTime,
  updatedAt: DateTime) {

  def save()(implicit session: DBSession): ResourceComment = ResourceComment.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = ResourceComment.destroy(this)(session)

}


object ResourceComment extends SQLSyntaxSupport[ResourceComment] {

  override val tableName = "resource_comments"

  override val columns = Seq("id", "resource_id", "user_id", "name", "password", "body", "created_at", "updated_at")

  def apply(rc: SyntaxProvider[ResourceComment])(rs: WrappedResultSet): ResourceComment = apply(rc.resultName)(rs)
  def apply(rc: ResultName[ResourceComment])(rs: WrappedResultSet): ResourceComment = new ResourceComment(
    id = rs.get(rc.id),
    resourceId = rs.get(rc.resourceId),
    userId = rs.get(rc.userId),
    name = rs.get(rc.name),
    password = rs.get(rc.password),
    body = rs.get(rc.body),
    createdAt = rs.get(rc.createdAt),
    updatedAt = rs.get(rc.updatedAt)
  )

  val rc = ResourceComment.syntax("rc")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[ResourceComment] = {
    withSQL {
      select.from(ResourceComment as rc).where.eq(rc.id, id)
    }.map(ResourceComment(rc.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ResourceComment] = {
    withSQL(select.from(ResourceComment as rc)).map(ResourceComment(rc.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ResourceComment as rc)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ResourceComment] = {
    withSQL {
      select.from(ResourceComment as rc).where.append(where)
    }.map(ResourceComment(rc.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ResourceComment] = {
    withSQL {
      select.from(ResourceComment as rc).where.append(where)
    }.map(ResourceComment(rc.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ResourceComment as rc).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    resourceId: Int,
    userId: Option[Int] = None,
    name: Option[String] = None,
    password: Option[String] = None,
    body: String,
    createdAt: DateTime,
    updatedAt: DateTime)(implicit session: DBSession): ResourceComment = {
    val generatedKey = withSQL {
      insert.into(ResourceComment).columns(
        column.resourceId,
        column.userId,
        column.name,
        column.password,
        column.body,
        column.createdAt,
        column.updatedAt
      ).values(
        resourceId,
        userId,
        name,
        password,
        body,
        createdAt,
        updatedAt
      )
    }.updateAndReturnGeneratedKey.apply()

    ResourceComment(
      id = generatedKey.toInt,
      resourceId = resourceId,
      userId = userId,
      name = name,
      password = password,
      body = body,
      createdAt = createdAt,
      updatedAt = updatedAt)
  }

  def batchInsert(entities: Seq[ResourceComment])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'resourceId -> entity.resourceId,
        'userId -> entity.userId,
        'name -> entity.name,
        'password -> entity.password,
        'body -> entity.body,
        'createdAt -> entity.createdAt,
        'updatedAt -> entity.updatedAt))
        SQL("""insert into resource_comments(
        resource_id,
        user_id,
        name,
        password,
        body,
        created_at,
        updated_at
      ) values (
        {resourceId},
        {userId},
        {name},
        {password},
        {body},
        {createdAt},
        {updatedAt}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: ResourceComment)(implicit session: DBSession): ResourceComment = {
    withSQL {
      update(ResourceComment).set(
        column.id -> entity.id,
        column.resourceId -> entity.resourceId,
        column.userId -> entity.userId,
        column.name -> entity.name,
        column.password -> entity.password,
        column.body -> entity.body,
        column.createdAt -> entity.createdAt,
        column.updatedAt -> entity.updatedAt
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ResourceComment)(implicit session: DBSession): Unit = {
    withSQL { delete.from(ResourceComment).where.eq(column.id, entity.id) }.update.apply()
  }

}
