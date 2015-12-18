package andon.api.models.generated

import scalikejdbc._
import org.joda.time.{DateTime}

case class ClassArticle(
  id: Int,
  classId: Short,
  latestRevisionNumber: Short,
  status: String,
  createdBy: Option[Int] = None,
  updatedBy: Option[Int] = None,
  createdAt: DateTime,
  updatedAt: DateTime) {

  def save()(implicit session: DBSession): ClassArticle = ClassArticle.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = ClassArticle.destroy(this)(session)

}


object ClassArticle extends SQLSyntaxSupport[ClassArticle] {

  override val tableName = "class_articles"

  override val columns = Seq("id", "class_id", "latest_revision_number", "status", "created_by", "updated_by", "created_at", "updated_at")

  def apply(ca: SyntaxProvider[ClassArticle])(rs: WrappedResultSet): ClassArticle = apply(ca.resultName)(rs)
  def apply(ca: ResultName[ClassArticle])(rs: WrappedResultSet): ClassArticle = new ClassArticle(
    id = rs.get(ca.id),
    classId = rs.get(ca.classId),
    latestRevisionNumber = rs.get(ca.latestRevisionNumber),
    status = rs.get(ca.status),
    createdBy = rs.get(ca.createdBy),
    updatedBy = rs.get(ca.updatedBy),
    createdAt = rs.get(ca.createdAt),
    updatedAt = rs.get(ca.updatedAt)
  )

  val ca = ClassArticle.syntax("ca")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[ClassArticle] = {
    withSQL {
      select.from(ClassArticle as ca).where.eq(ca.id, id)
    }.map(ClassArticle(ca.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ClassArticle] = {
    withSQL(select.from(ClassArticle as ca)).map(ClassArticle(ca.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ClassArticle as ca)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ClassArticle] = {
    withSQL {
      select.from(ClassArticle as ca).where.append(where)
    }.map(ClassArticle(ca.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ClassArticle] = {
    withSQL {
      select.from(ClassArticle as ca).where.append(where)
    }.map(ClassArticle(ca.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ClassArticle as ca).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    classId: Short,
    latestRevisionNumber: Short,
    status: String,
    createdBy: Option[Int] = None,
    updatedBy: Option[Int] = None,
    createdAt: DateTime,
    updatedAt: DateTime)(implicit session: DBSession): ClassArticle = {
    val generatedKey = withSQL {
      insert.into(ClassArticle).columns(
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

    ClassArticle(
      id = generatedKey.toInt,
      classId = classId,
      latestRevisionNumber = latestRevisionNumber,
      status = status,
      createdBy = createdBy,
      updatedBy = updatedBy,
      createdAt = createdAt,
      updatedAt = updatedAt)
  }

  def batchInsert(entities: Seq[ClassArticle])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'classId -> entity.classId,
        'latestRevisionNumber -> entity.latestRevisionNumber,
        'status -> entity.status,
        'createdBy -> entity.createdBy,
        'updatedBy -> entity.updatedBy,
        'createdAt -> entity.createdAt,
        'updatedAt -> entity.updatedAt))
        SQL("""insert into class_articles(
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

  def save(entity: ClassArticle)(implicit session: DBSession): ClassArticle = {
    withSQL {
      update(ClassArticle).set(
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

  def destroy(entity: ClassArticle)(implicit session: DBSession): Unit = {
    withSQL { delete.from(ClassArticle).where.eq(column.id, entity.id) }.update.apply()
  }

}
