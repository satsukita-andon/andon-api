package andon.api.models.generated

import scalikejdbc._
import org.joda.time.{DateTime}

case class ClassImage(
  id: Int,
  classId: Short,
  userId: Int,
  url: String,
  createdAt: DateTime) {

  def save()(implicit session: DBSession): ClassImage = ClassImage.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = ClassImage.destroy(this)(session)

}


object ClassImage extends SQLSyntaxSupport[ClassImage] {

  override val tableName = "class_images"

  override val columns = Seq("id", "class_id", "user_id", "url", "created_at")

  def apply(ci: SyntaxProvider[ClassImage])(rs: WrappedResultSet): ClassImage = apply(ci.resultName)(rs)
  def apply(ci: ResultName[ClassImage])(rs: WrappedResultSet): ClassImage = new ClassImage(
    id = rs.get(ci.id),
    classId = rs.get(ci.classId),
    userId = rs.get(ci.userId),
    url = rs.get(ci.url),
    createdAt = rs.get(ci.createdAt)
  )

  val ci = ClassImage.syntax("ci")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[ClassImage] = {
    withSQL {
      select.from(ClassImage as ci).where.eq(ci.id, id)
    }.map(ClassImage(ci.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ClassImage] = {
    withSQL(select.from(ClassImage as ci)).map(ClassImage(ci.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ClassImage as ci)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ClassImage] = {
    withSQL {
      select.from(ClassImage as ci).where.append(where)
    }.map(ClassImage(ci.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ClassImage] = {
    withSQL {
      select.from(ClassImage as ci).where.append(where)
    }.map(ClassImage(ci.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ClassImage as ci).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    classId: Short,
    userId: Int,
    url: String,
    createdAt: DateTime)(implicit session: DBSession): ClassImage = {
    val generatedKey = withSQL {
      insert.into(ClassImage).columns(
        column.classId,
        column.userId,
        column.url,
        column.createdAt
      ).values(
        classId,
        userId,
        url,
        createdAt
      )
    }.updateAndReturnGeneratedKey.apply()

    ClassImage(
      id = generatedKey.toInt,
      classId = classId,
      userId = userId,
      url = url,
      createdAt = createdAt)
  }

  def batchInsert(entities: Seq[ClassImage])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'classId -> entity.classId,
        'userId -> entity.userId,
        'url -> entity.url,
        'createdAt -> entity.createdAt))
        SQL("""insert into class_images(
        class_id,
        user_id,
        url,
        created_at
      ) values (
        {classId},
        {userId},
        {url},
        {createdAt}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: ClassImage)(implicit session: DBSession): ClassImage = {
    withSQL {
      update(ClassImage).set(
        column.id -> entity.id,
        column.classId -> entity.classId,
        column.userId -> entity.userId,
        column.url -> entity.url,
        column.createdAt -> entity.createdAt
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ClassImage)(implicit session: DBSession): Unit = {
    withSQL { delete.from(ClassImage).where.eq(column.id, entity.id) }.update.apply()
  }

}
