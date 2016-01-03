package andon.api.models.generated

import scalikejdbc._
import org.joda.time.{DateTime}

case class ClassReview(
  id: Int,
  classId: Short,
  userId: Int,
  title: String,
  body: String,
  score: Option[BigDecimal] = None,
  status: String,
  createdAt: DateTime,
  updatedAt: DateTime) {

  def save()(implicit session: DBSession): ClassReview = ClassReview.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = ClassReview.destroy(this)(session)

}


object ClassReview extends SQLSyntaxSupport[ClassReview] {

  override val tableName = "class_reviews"

  override val columns = Seq("id", "class_id", "user_id", "title", "body", "score", "status", "created_at", "updated_at")

  def apply(cr: SyntaxProvider[ClassReview])(rs: WrappedResultSet): ClassReview = apply(cr.resultName)(rs)
  def apply(cr: ResultName[ClassReview])(rs: WrappedResultSet): ClassReview = new ClassReview(
    id = rs.get(cr.id),
    classId = rs.get(cr.classId),
    userId = rs.get(cr.userId),
    title = rs.get(cr.title),
    body = rs.get(cr.body),
    score = rs.get(cr.score),
    status = rs.get(cr.status),
    createdAt = rs.get(cr.createdAt),
    updatedAt = rs.get(cr.updatedAt)
  )

  val cr = ClassReview.syntax("cr")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[ClassReview] = {
    withSQL {
      select.from(ClassReview as cr).where.eq(cr.id, id)
    }.map(ClassReview(cr.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ClassReview] = {
    withSQL(select.from(ClassReview as cr)).map(ClassReview(cr.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ClassReview as cr)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ClassReview] = {
    withSQL {
      select.from(ClassReview as cr).where.append(where)
    }.map(ClassReview(cr.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ClassReview] = {
    withSQL {
      select.from(ClassReview as cr).where.append(where)
    }.map(ClassReview(cr.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ClassReview as cr).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    classId: Short,
    userId: Int,
    title: String,
    body: String,
    score: Option[BigDecimal] = None,
    status: String,
    createdAt: DateTime,
    updatedAt: DateTime)(implicit session: DBSession): ClassReview = {
    val generatedKey = withSQL {
      insert.into(ClassReview).columns(
        column.classId,
        column.userId,
        column.title,
        column.body,
        column.score,
        column.status,
        column.createdAt,
        column.updatedAt
      ).values(
        classId,
        userId,
        title,
        body,
        score,
        status,
        createdAt,
        updatedAt
      )
    }.updateAndReturnGeneratedKey.apply()

    ClassReview(
      id = generatedKey.toInt,
      classId = classId,
      userId = userId,
      title = title,
      body = body,
      score = score,
      status = status,
      createdAt = createdAt,
      updatedAt = updatedAt)
  }

  def batchInsert(entities: Seq[ClassReview])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'classId -> entity.classId,
        'userId -> entity.userId,
        'title -> entity.title,
        'body -> entity.body,
        'score -> entity.score,
        'status -> entity.status,
        'createdAt -> entity.createdAt,
        'updatedAt -> entity.updatedAt))
        SQL("""insert into class_reviews(
        class_id,
        user_id,
        title,
        body,
        score,
        status,
        created_at,
        updated_at
      ) values (
        {classId},
        {userId},
        {title},
        {body},
        {score},
        {status},
        {createdAt},
        {updatedAt}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: ClassReview)(implicit session: DBSession): ClassReview = {
    withSQL {
      update(ClassReview).set(
        column.id -> entity.id,
        column.classId -> entity.classId,
        column.userId -> entity.userId,
        column.title -> entity.title,
        column.body -> entity.body,
        column.score -> entity.score,
        column.status -> entity.status,
        column.createdAt -> entity.createdAt,
        column.updatedAt -> entity.updatedAt
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ClassReview)(implicit session: DBSession): Unit = {
    withSQL { delete.from(ClassReview).where.eq(column.id, entity.id) }.update.apply()
  }

}
