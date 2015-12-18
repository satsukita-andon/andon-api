package andon.api.models.generated

import scalikejdbc._
import org.joda.time.{DateTime}

case class ClassArticleRevision(
  id: Int,
  articleId: Int,
  revisionNumber: Short,
  userId: Option[Int] = None,
  title: String,
  body: String,
  comment: String,
  createdAt: DateTime) {

  def save()(implicit session: DBSession): ClassArticleRevision = ClassArticleRevision.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = ClassArticleRevision.destroy(this)(session)

}


object ClassArticleRevision extends SQLSyntaxSupport[ClassArticleRevision] {

  override val tableName = "class_article_revisions"

  override val columns = Seq("id", "article_id", "revision_number", "user_id", "title", "body", "comment", "created_at")

  def apply(car: SyntaxProvider[ClassArticleRevision])(rs: WrappedResultSet): ClassArticleRevision = apply(car.resultName)(rs)
  def apply(car: ResultName[ClassArticleRevision])(rs: WrappedResultSet): ClassArticleRevision = new ClassArticleRevision(
    id = rs.get(car.id),
    articleId = rs.get(car.articleId),
    revisionNumber = rs.get(car.revisionNumber),
    userId = rs.get(car.userId),
    title = rs.get(car.title),
    body = rs.get(car.body),
    comment = rs.get(car.comment),
    createdAt = rs.get(car.createdAt)
  )

  val car = ClassArticleRevision.syntax("car")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[ClassArticleRevision] = {
    withSQL {
      select.from(ClassArticleRevision as car).where.eq(car.id, id)
    }.map(ClassArticleRevision(car.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ClassArticleRevision] = {
    withSQL(select.from(ClassArticleRevision as car)).map(ClassArticleRevision(car.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ClassArticleRevision as car)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ClassArticleRevision] = {
    withSQL {
      select.from(ClassArticleRevision as car).where.append(where)
    }.map(ClassArticleRevision(car.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ClassArticleRevision] = {
    withSQL {
      select.from(ClassArticleRevision as car).where.append(where)
    }.map(ClassArticleRevision(car.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ClassArticleRevision as car).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    articleId: Int,
    revisionNumber: Short,
    userId: Option[Int] = None,
    title: String,
    body: String,
    comment: String,
    createdAt: DateTime)(implicit session: DBSession): ClassArticleRevision = {
    val generatedKey = withSQL {
      insert.into(ClassArticleRevision).columns(
        column.articleId,
        column.revisionNumber,
        column.userId,
        column.title,
        column.body,
        column.comment,
        column.createdAt
      ).values(
        articleId,
        revisionNumber,
        userId,
        title,
        body,
        comment,
        createdAt
      )
    }.updateAndReturnGeneratedKey.apply()

    ClassArticleRevision(
      id = generatedKey.toInt,
      articleId = articleId,
      revisionNumber = revisionNumber,
      userId = userId,
      title = title,
      body = body,
      comment = comment,
      createdAt = createdAt)
  }

  def batchInsert(entities: Seq[ClassArticleRevision])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'articleId -> entity.articleId,
        'revisionNumber -> entity.revisionNumber,
        'userId -> entity.userId,
        'title -> entity.title,
        'body -> entity.body,
        'comment -> entity.comment,
        'createdAt -> entity.createdAt))
        SQL("""insert into class_article_revisions(
        article_id,
        revision_number,
        user_id,
        title,
        body,
        comment,
        created_at
      ) values (
        {articleId},
        {revisionNumber},
        {userId},
        {title},
        {body},
        {comment},
        {createdAt}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: ClassArticleRevision)(implicit session: DBSession): ClassArticleRevision = {
    withSQL {
      update(ClassArticleRevision).set(
        column.id -> entity.id,
        column.articleId -> entity.articleId,
        column.revisionNumber -> entity.revisionNumber,
        column.userId -> entity.userId,
        column.title -> entity.title,
        column.body -> entity.body,
        column.comment -> entity.comment,
        column.createdAt -> entity.createdAt
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ClassArticleRevision)(implicit session: DBSession): Unit = {
    withSQL { delete.from(ClassArticleRevision).where.eq(column.id, entity.id) }.update.apply()
  }

}
