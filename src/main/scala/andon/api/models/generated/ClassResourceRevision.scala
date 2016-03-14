package andon.api.models.generated

import scalikejdbc._
import org.joda.time.{DateTime}

case class ClassResourceRevision(
  id: Int,
  resourceId: Int,
  revisionNumber: Short,
  userId: Option[Int] = None,
  title: String,
  description: String,
  url: String,
  comment: String,
  createdAt: DateTime) {

  def save()(implicit session: DBSession): ClassResourceRevision = ClassResourceRevision.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = ClassResourceRevision.destroy(this)(session)

}


object ClassResourceRevision extends SQLSyntaxSupport[ClassResourceRevision] {

  override val tableName = "class_resource_revisions"

  override val columns = Seq("id", "resource_id", "revision_number", "user_id", "title", "description", "url", "comment", "created_at")

  def apply(crr: SyntaxProvider[ClassResourceRevision])(rs: WrappedResultSet): ClassResourceRevision = apply(crr.resultName)(rs)
  def apply(crr: ResultName[ClassResourceRevision])(rs: WrappedResultSet): ClassResourceRevision = new ClassResourceRevision(
    id = rs.get(crr.id),
    resourceId = rs.get(crr.resourceId),
    revisionNumber = rs.get(crr.revisionNumber),
    userId = rs.get(crr.userId),
    title = rs.get(crr.title),
    description = rs.get(crr.description),
    url = rs.get(crr.url),
    comment = rs.get(crr.comment),
    createdAt = rs.get(crr.createdAt)
  )

  val crr = ClassResourceRevision.syntax("crr")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[ClassResourceRevision] = {
    withSQL {
      select.from(ClassResourceRevision as crr).where.eq(crr.id, id)
    }.map(ClassResourceRevision(crr.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ClassResourceRevision] = {
    withSQL(select.from(ClassResourceRevision as crr)).map(ClassResourceRevision(crr.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ClassResourceRevision as crr)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ClassResourceRevision] = {
    withSQL {
      select.from(ClassResourceRevision as crr).where.append(where)
    }.map(ClassResourceRevision(crr.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ClassResourceRevision] = {
    withSQL {
      select.from(ClassResourceRevision as crr).where.append(where)
    }.map(ClassResourceRevision(crr.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ClassResourceRevision as crr).where.append(where)
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
    createdAt: DateTime)(implicit session: DBSession): ClassResourceRevision = {
    val generatedKey = withSQL {
      insert.into(ClassResourceRevision).columns(
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

    ClassResourceRevision(
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

  def batchInsert(entities: Seq[ClassResourceRevision])(implicit session: DBSession): Seq[Int] = {
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
        SQL("""insert into class_resource_revisions(
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

  def save(entity: ClassResourceRevision)(implicit session: DBSession): ClassResourceRevision = {
    withSQL {
      update(ClassResourceRevision).set(
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

  def destroy(entity: ClassResourceRevision)(implicit session: DBSession): Unit = {
    withSQL { delete.from(ClassResourceRevision).where.eq(column.id, entity.id) }.update.apply()
  }

}
