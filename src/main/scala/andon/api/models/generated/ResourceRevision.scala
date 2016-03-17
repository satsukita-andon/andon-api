package andon.api.models.generated

import scalikejdbc._
import org.joda.time.{DateTime}

case class ResourceRevision(
  id: Int,
  resourceId: Int,
  revisionNumber: Short,
  userId: Option[Int] = None,
  title: String,
  description: String,
  url: String,
  comment: String,
  createdAt: DateTime) {

  def save()(implicit session: DBSession): ResourceRevision = ResourceRevision.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = ResourceRevision.destroy(this)(session)

}


object ResourceRevision extends SQLSyntaxSupport[ResourceRevision] {

  override val tableName = "resource_revisions"

  override val columns = Seq("id", "resource_id", "revision_number", "user_id", "title", "description", "url", "comment", "created_at")

  def apply(rr: SyntaxProvider[ResourceRevision])(rs: WrappedResultSet): ResourceRevision = apply(rr.resultName)(rs)
  def apply(rr: ResultName[ResourceRevision])(rs: WrappedResultSet): ResourceRevision = new ResourceRevision(
    id = rs.get(rr.id),
    resourceId = rs.get(rr.resourceId),
    revisionNumber = rs.get(rr.revisionNumber),
    userId = rs.get(rr.userId),
    title = rs.get(rr.title),
    description = rs.get(rr.description),
    url = rs.get(rr.url),
    comment = rs.get(rr.comment),
    createdAt = rs.get(rr.createdAt)
  )

  val rr = ResourceRevision.syntax("rr")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[ResourceRevision] = {
    withSQL {
      select.from(ResourceRevision as rr).where.eq(rr.id, id)
    }.map(ResourceRevision(rr.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ResourceRevision] = {
    withSQL(select.from(ResourceRevision as rr)).map(ResourceRevision(rr.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ResourceRevision as rr)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ResourceRevision] = {
    withSQL {
      select.from(ResourceRevision as rr).where.append(where)
    }.map(ResourceRevision(rr.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ResourceRevision] = {
    withSQL {
      select.from(ResourceRevision as rr).where.append(where)
    }.map(ResourceRevision(rr.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ResourceRevision as rr).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    resourceId: Int,
    revisionNumber: Short,
    userId: Option[Int] = None,
    title: String,
    description: String,
    url: String,
    comment: String,
    createdAt: DateTime)(implicit session: DBSession): ResourceRevision = {
    val generatedKey = withSQL {
      insert.into(ResourceRevision).columns(
        column.resourceId,
        column.revisionNumber,
        column.userId,
        column.title,
        column.description,
        column.url,
        column.comment,
        column.createdAt
      ).values(
        resourceId,
        revisionNumber,
        userId,
        title,
        description,
        url,
        comment,
        createdAt
      )
    }.updateAndReturnGeneratedKey.apply()

    ResourceRevision(
      id = generatedKey.toInt,
      resourceId = resourceId,
      revisionNumber = revisionNumber,
      userId = userId,
      title = title,
      description = description,
      url = url,
      comment = comment,
      createdAt = createdAt)
  }

  def batchInsert(entities: Seq[ResourceRevision])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'resourceId -> entity.resourceId,
        'revisionNumber -> entity.revisionNumber,
        'userId -> entity.userId,
        'title -> entity.title,
        'description -> entity.description,
        'url -> entity.url,
        'comment -> entity.comment,
        'createdAt -> entity.createdAt))
        SQL("""insert into resource_revisions(
        resource_id,
        revision_number,
        user_id,
        title,
        description,
        url,
        comment,
        created_at
      ) values (
        {resourceId},
        {revisionNumber},
        {userId},
        {title},
        {description},
        {url},
        {comment},
        {createdAt}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: ResourceRevision)(implicit session: DBSession): ResourceRevision = {
    withSQL {
      update(ResourceRevision).set(
        column.id -> entity.id,
        column.resourceId -> entity.resourceId,
        column.revisionNumber -> entity.revisionNumber,
        column.userId -> entity.userId,
        column.title -> entity.title,
        column.description -> entity.description,
        column.url -> entity.url,
        column.comment -> entity.comment,
        column.createdAt -> entity.createdAt
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ResourceRevision)(implicit session: DBSession): Unit = {
    withSQL { delete.from(ResourceRevision).where.eq(column.id, entity.id) }.update.apply()
  }

}
