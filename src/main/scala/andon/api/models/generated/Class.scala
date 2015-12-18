package andon.api.models.generated

import scalikejdbc._
import org.joda.time.{DateTime}

case class Class(
  id: Short,
  times: Short,
  grade: Short,
  `class`: Short,
  title: String,
  titleKana: Option[String] = None,
  description: Option[String] = None,
  score: Option[BigDecimal] = None,
  headerImageUrl: Option[String] = None,
  thumbnailUrl: Option[String] = None,
  createdAt: DateTime,
  updatedAt: DateTime) {

  def save()(implicit session: DBSession): Class = Class.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = Class.destroy(this)(session)

}


object Class extends SQLSyntaxSupport[Class] {

  override val tableName = "classes"

  override val columns = Seq("id", "times", "grade", "class", "title", "title_kana", "description", "score", "header_image_url", "thumbnail_url", "created_at", "updated_at")

  def apply(c: SyntaxProvider[Class])(rs: WrappedResultSet): Class = apply(c.resultName)(rs)
  def apply(c: ResultName[Class])(rs: WrappedResultSet): Class = new Class(
    id = rs.get(c.id),
    times = rs.get(c.times),
    grade = rs.get(c.grade),
    `class` = rs.get(c.`class`),
    title = rs.get(c.title),
    titleKana = rs.get(c.titleKana),
    description = rs.get(c.description),
    score = rs.get(c.score),
    headerImageUrl = rs.get(c.headerImageUrl),
    thumbnailUrl = rs.get(c.thumbnailUrl),
    createdAt = rs.get(c.createdAt),
    updatedAt = rs.get(c.updatedAt)
  )

  val c = Class.syntax("c")

  override val autoSession = AutoSession

  def find(id: Short)(implicit session: DBSession): Option[Class] = {
    withSQL {
      select.from(Class as c).where.eq(c.id, id)
    }.map(Class(c.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[Class] = {
    withSQL(select.from(Class as c)).map(Class(c.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(Class as c)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[Class] = {
    withSQL {
      select.from(Class as c).where.append(where)
    }.map(Class(c.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[Class] = {
    withSQL {
      select.from(Class as c).where.append(where)
    }.map(Class(c.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(Class as c).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    times: Short,
    grade: Short,
    `class`: Short,
    title: String,
    titleKana: Option[String] = None,
    description: Option[String] = None,
    score: Option[BigDecimal] = None,
    headerImageUrl: Option[String] = None,
    thumbnailUrl: Option[String] = None,
    createdAt: DateTime,
    updatedAt: DateTime)(implicit session: DBSession): Class = {
    val generatedKey = withSQL {
      insert.into(Class).columns(
        column.times,
        column.grade,
        column.`class`,
        column.title,
        column.titleKana,
        column.description,
        column.score,
        column.headerImageUrl,
        column.thumbnailUrl,
        column.createdAt,
        column.updatedAt
      ).values(
        times,
        grade,
        `class`,
        title,
        titleKana,
        description,
        score,
        headerImageUrl,
        thumbnailUrl,
        createdAt,
        updatedAt
      )
    }.updateAndReturnGeneratedKey.apply()

    Class(
      id = generatedKey.toShort,
      times = times,
      grade = grade,
      `class` = `class`,
      title = title,
      titleKana = titleKana,
      description = description,
      score = score,
      headerImageUrl = headerImageUrl,
      thumbnailUrl = thumbnailUrl,
      createdAt = createdAt,
      updatedAt = updatedAt)
  }

  def batchInsert(entities: Seq[Class])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'times -> entity.times,
        'grade -> entity.grade,
        'class -> entity.`class`,
        'title -> entity.title,
        'titleKana -> entity.titleKana,
        'description -> entity.description,
        'score -> entity.score,
        'headerImageUrl -> entity.headerImageUrl,
        'thumbnailUrl -> entity.thumbnailUrl,
        'createdAt -> entity.createdAt,
        'updatedAt -> entity.updatedAt))
        SQL("""insert into classes(
        times,
        grade,
        class,
        title,
        title_kana,
        description,
        score,
        header_image_url,
        thumbnail_url,
        created_at,
        updated_at
      ) values (
        {times},
        {grade},
        {class},
        {title},
        {titleKana},
        {description},
        {score},
        {headerImageUrl},
        {thumbnailUrl},
        {createdAt},
        {updatedAt}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: Class)(implicit session: DBSession): Class = {
    withSQL {
      update(Class).set(
        column.id -> entity.id,
        column.times -> entity.times,
        column.grade -> entity.grade,
        column.`class` -> entity.`class`,
        column.title -> entity.title,
        column.titleKana -> entity.titleKana,
        column.description -> entity.description,
        column.score -> entity.score,
        column.headerImageUrl -> entity.headerImageUrl,
        column.thumbnailUrl -> entity.thumbnailUrl,
        column.createdAt -> entity.createdAt,
        column.updatedAt -> entity.updatedAt
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Class)(implicit session: DBSession): Unit = {
    withSQL { delete.from(Class).where.eq(column.id, entity.id) }.update.apply()
  }

}
