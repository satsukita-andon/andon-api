package andon.api.models.generated

import scalikejdbc._
import org.joda.time.{DateTime}

case class ArticleRevision(
  id: Int,
  articleId: Int,
  revisionNumber: Short,
  userId: Option[Int] = None,
  title: String,
  body: String,
  comment: String,
  createdAt: DateTime) {

  def save()(implicit session: DBSession): ArticleRevision = ArticleRevision.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = ArticleRevision.destroy(this)(session)

}


object ArticleRevision extends SQLSyntaxSupport[ArticleRevision] {

  override val tableName = "article_revisions"

  override val columns = Seq("id", "article_id", "revision_number", "user_id", "title", "body", "comment", "created_at")

  def apply(ar: SyntaxProvider[ArticleRevision])(rs: WrappedResultSet): ArticleRevision = apply(ar.resultName)(rs)
  def apply(ar: ResultName[ArticleRevision])(rs: WrappedResultSet): ArticleRevision = new ArticleRevision(
    id = rs.get(ar.id),
    articleId = rs.get(ar.articleId),
    revisionNumber = rs.get(ar.revisionNumber),
    userId = rs.get(ar.userId),
    title = rs.get(ar.title),
    body = rs.get(ar.body),
    comment = rs.get(ar.comment),
    createdAt = rs.get(ar.createdAt)
  )

  val ar = ArticleRevision.syntax("ar")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[ArticleRevision] = {
    withSQL {
      select.from(ArticleRevision as ar).where.eq(ar.id, id)
    }.map(ArticleRevision(ar.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ArticleRevision] = {
    withSQL(select.from(ArticleRevision as ar)).map(ArticleRevision(ar.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ArticleRevision as ar)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ArticleRevision] = {
    withSQL {
      select.from(ArticleRevision as ar).where.append(where)
    }.map(ArticleRevision(ar.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ArticleRevision] = {
    withSQL {
      select.from(ArticleRevision as ar).where.append(where)
    }.map(ArticleRevision(ar.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ArticleRevision as ar).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    articleId: Int,
    revisionNumber: Short,
    userId: Option[Int] = None,
    title: String,
    body: String,
    comment: String,
    createdAt: DateTime)(implicit session: DBSession): ArticleRevision = {
    val generatedKey = withSQL {
      insert.into(ArticleRevision).columns(
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

    ArticleRevision(
      id = generatedKey.toInt,
      articleId = articleId,
      revisionNumber = revisionNumber,
      userId = userId,
      title = title,
      body = body,
      comment = comment,
      createdAt = createdAt)
  }

  def batchInsert(entities: Seq[ArticleRevision])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'articleId -> entity.articleId,
        'revisionNumber -> entity.revisionNumber,
        'userId -> entity.userId,
        'title -> entity.title,
        'body -> entity.body,
        'comment -> entity.comment,
        'createdAt -> entity.createdAt))
        SQL("""insert into article_revisions(
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

  def save(entity: ArticleRevision)(implicit session: DBSession): ArticleRevision = {
    withSQL {
      update(ArticleRevision).set(
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

  def destroy(entity: ArticleRevision)(implicit session: DBSession): Unit = {
    withSQL { delete.from(ArticleRevision).where.eq(column.id, entity.id) }.update.apply()
  }

}
