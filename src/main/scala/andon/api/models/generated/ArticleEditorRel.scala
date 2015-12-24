package andon.api.models.generated

import scalikejdbc._

case class ArticleEditorRel(
  id: Int,
  articleId: Int,
  userId: Int) {

  def save()(implicit session: DBSession): ArticleEditorRel = ArticleEditorRel.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = ArticleEditorRel.destroy(this)(session)

}


object ArticleEditorRel extends SQLSyntaxSupport[ArticleEditorRel] {

  override val tableName = "article_editor_rel"

  override val columns = Seq("id", "article_id", "user_id")

  def apply(aer: SyntaxProvider[ArticleEditorRel])(rs: WrappedResultSet): ArticleEditorRel = apply(aer.resultName)(rs)
  def apply(aer: ResultName[ArticleEditorRel])(rs: WrappedResultSet): ArticleEditorRel = new ArticleEditorRel(
    id = rs.get(aer.id),
    articleId = rs.get(aer.articleId),
    userId = rs.get(aer.userId)
  )

  val aer = ArticleEditorRel.syntax("aer")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[ArticleEditorRel] = {
    withSQL {
      select.from(ArticleEditorRel as aer).where.eq(aer.id, id)
    }.map(ArticleEditorRel(aer.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ArticleEditorRel] = {
    withSQL(select.from(ArticleEditorRel as aer)).map(ArticleEditorRel(aer.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ArticleEditorRel as aer)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ArticleEditorRel] = {
    withSQL {
      select.from(ArticleEditorRel as aer).where.append(where)
    }.map(ArticleEditorRel(aer.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ArticleEditorRel] = {
    withSQL {
      select.from(ArticleEditorRel as aer).where.append(where)
    }.map(ArticleEditorRel(aer.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ArticleEditorRel as aer).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    articleId: Int,
    userId: Int)(implicit session: DBSession): ArticleEditorRel = {
    val generatedKey = withSQL {
      insert.into(ArticleEditorRel).columns(
        column.articleId,
        column.userId
      ).values(
        articleId,
        userId
      )
    }.updateAndReturnGeneratedKey.apply()

    ArticleEditorRel(
      id = generatedKey.toInt,
      articleId = articleId,
      userId = userId)
  }

  def batchInsert(entities: Seq[ArticleEditorRel])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'articleId -> entity.articleId,
        'userId -> entity.userId))
        SQL("""insert into article_editor_rel(
        article_id,
        user_id
      ) values (
        {articleId},
        {userId}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: ArticleEditorRel)(implicit session: DBSession): ArticleEditorRel = {
    withSQL {
      update(ArticleEditorRel).set(
        column.id -> entity.id,
        column.articleId -> entity.articleId,
        column.userId -> entity.userId
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ArticleEditorRel)(implicit session: DBSession): Unit = {
    withSQL { delete.from(ArticleEditorRel).where.eq(column.id, entity.id) }.update.apply()
  }

}
