package andon.api.models.generated

import scalikejdbc._

case class ArticleTag(
  id: Int,
  articleId: Int,
  label: String) {

  def save()(implicit session: DBSession): ArticleTag = ArticleTag.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = ArticleTag.destroy(this)(session)

}


object ArticleTag extends SQLSyntaxSupport[ArticleTag] {

  override val tableName = "article_tags"

  override val columns = Seq("id", "article_id", "label")

  def apply(at: SyntaxProvider[ArticleTag])(rs: WrappedResultSet): ArticleTag = apply(at.resultName)(rs)
  def apply(at: ResultName[ArticleTag])(rs: WrappedResultSet): ArticleTag = new ArticleTag(
    id = rs.get(at.id),
    articleId = rs.get(at.articleId),
    label = rs.get(at.label)
  )

  val at = ArticleTag.syntax("at")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[ArticleTag] = {
    withSQL {
      select.from(ArticleTag as at).where.eq(at.id, id)
    }.map(ArticleTag(at.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ArticleTag] = {
    withSQL(select.from(ArticleTag as at)).map(ArticleTag(at.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ArticleTag as at)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ArticleTag] = {
    withSQL {
      select.from(ArticleTag as at).where.append(where)
    }.map(ArticleTag(at.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ArticleTag] = {
    withSQL {
      select.from(ArticleTag as at).where.append(where)
    }.map(ArticleTag(at.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ArticleTag as at).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    articleId: Int,
    label: String)(implicit session: DBSession): ArticleTag = {
    val generatedKey = withSQL {
      insert.into(ArticleTag).columns(
        column.articleId,
        column.label
      ).values(
        articleId,
        label
      )
    }.updateAndReturnGeneratedKey.apply()

    ArticleTag(
      id = generatedKey.toInt,
      articleId = articleId,
      label = label)
  }

  def batchInsert(entities: Seq[ArticleTag])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'articleId -> entity.articleId,
        'label -> entity.label))
        SQL("""insert into article_tags(
        article_id,
        label
      ) values (
        {articleId},
        {label}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: ArticleTag)(implicit session: DBSession): ArticleTag = {
    withSQL {
      update(ArticleTag).set(
        column.id -> entity.id,
        column.articleId -> entity.articleId,
        column.label -> entity.label
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ArticleTag)(implicit session: DBSession): Unit = {
    withSQL { delete.from(ArticleTag).where.eq(column.id, entity.id) }.update.apply()
  }

}
