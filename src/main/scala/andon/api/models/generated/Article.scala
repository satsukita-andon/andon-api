package andon.api.models.generated

import scalikejdbc._
import org.joda.time.{DateTime}

case class Article(
  id: Int,
  ownerId: Int,
  latestRevisionNumber: Short,
  status: String,
  editorialRight: String,
  createdBy: Option[Int] = None,
  updatedBy: Option[Int] = None,
  createdAt: DateTime,
  updatedAt: DateTime) {

  def save()(implicit session: DBSession): Article = Article.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = Article.destroy(this)(session)

}


object Article extends SQLSyntaxSupport[Article] {

  override val tableName = "articles"

  override val columns = Seq("id", "owner_id", "latest_revision_number", "status", "editorial_right", "created_by", "updated_by", "created_at", "updated_at")

  def apply(a: SyntaxProvider[Article])(rs: WrappedResultSet): Article = apply(a.resultName)(rs)
  def apply(a: ResultName[Article])(rs: WrappedResultSet): Article = new Article(
    id = rs.get(a.id),
    ownerId = rs.get(a.ownerId),
    latestRevisionNumber = rs.get(a.latestRevisionNumber),
    status = rs.get(a.status),
    editorialRight = rs.get(a.editorialRight),
    createdBy = rs.get(a.createdBy),
    updatedBy = rs.get(a.updatedBy),
    createdAt = rs.get(a.createdAt),
    updatedAt = rs.get(a.updatedAt)
  )

  val a = Article.syntax("a")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[Article] = {
    withSQL {
      select.from(Article as a).where.eq(a.id, id)
    }.map(Article(a.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[Article] = {
    withSQL(select.from(Article as a)).map(Article(a.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(Article as a)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[Article] = {
    withSQL {
      select.from(Article as a).where.append(where)
    }.map(Article(a.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[Article] = {
    withSQL {
      select.from(Article as a).where.append(where)
    }.map(Article(a.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(Article as a).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    ownerId: Int,
    latestRevisionNumber: Short,
    status: String,
    editorialRight: String,
    createdBy: Option[Int] = None,
    updatedBy: Option[Int] = None,
    createdAt: DateTime,
    updatedAt: DateTime)(implicit session: DBSession): Article = {
    val generatedKey = withSQL {
      insert.into(Article).columns(
        column.ownerId,
        column.latestRevisionNumber,
        column.status,
        column.editorialRight,
        column.createdBy,
        column.updatedBy,
        column.createdAt,
        column.updatedAt
      ).values(
        ownerId,
        latestRevisionNumber,
        status,
        editorialRight,
        createdBy,
        updatedBy,
        createdAt,
        updatedAt
      )
    }.updateAndReturnGeneratedKey.apply()

    Article(
      id = generatedKey.toInt,
      ownerId = ownerId,
      latestRevisionNumber = latestRevisionNumber,
      status = status,
      editorialRight = editorialRight,
      createdBy = createdBy,
      updatedBy = updatedBy,
      createdAt = createdAt,
      updatedAt = updatedAt)
  }

  def batchInsert(entities: Seq[Article])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'ownerId -> entity.ownerId,
        'latestRevisionNumber -> entity.latestRevisionNumber,
        'status -> entity.status,
        'editorialRight -> entity.editorialRight,
        'createdBy -> entity.createdBy,
        'updatedBy -> entity.updatedBy,
        'createdAt -> entity.createdAt,
        'updatedAt -> entity.updatedAt))
        SQL("""insert into articles(
        owner_id,
        latest_revision_number,
        status,
        editorial_right,
        created_by,
        updated_by,
        created_at,
        updated_at
      ) values (
        {ownerId},
        {latestRevisionNumber},
        {status},
        {editorialRight},
        {createdBy},
        {updatedBy},
        {createdAt},
        {updatedAt}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: Article)(implicit session: DBSession): Article = {
    withSQL {
      update(Article).set(
        column.id -> entity.id,
        column.ownerId -> entity.ownerId,
        column.latestRevisionNumber -> entity.latestRevisionNumber,
        column.status -> entity.status,
        column.editorialRight -> entity.editorialRight,
        column.createdBy -> entity.createdBy,
        column.updatedBy -> entity.updatedBy,
        column.createdAt -> entity.createdAt,
        column.updatedAt -> entity.updatedAt
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Article)(implicit session: DBSession): Unit = {
    withSQL { delete.from(Article).where.eq(column.id, entity.id) }.update.apply()
  }

}
