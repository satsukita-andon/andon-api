package andon.api.models.generated

import scalikejdbc._

case class Festival(
  id: Short,
  times: Short,
  theme: String,
  themeRoman: String,
  themeKana: String,
  thumbnailUrl: Option[String] = None) {

  def save()(implicit session: DBSession): Festival = Festival.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = Festival.destroy(this)(session)

}


object Festival extends SQLSyntaxSupport[Festival] {

  override val tableName = "festivals"

  override val columns = Seq("id", "times", "theme", "theme_roman", "theme_kana", "thumbnail_url")

  def apply(f: SyntaxProvider[Festival])(rs: WrappedResultSet): Festival = apply(f.resultName)(rs)
  def apply(f: ResultName[Festival])(rs: WrappedResultSet): Festival = new Festival(
    id = rs.get(f.id),
    times = rs.get(f.times),
    theme = rs.get(f.theme),
    themeRoman = rs.get(f.themeRoman),
    themeKana = rs.get(f.themeKana),
    thumbnailUrl = rs.get(f.thumbnailUrl)
  )

  val f = Festival.syntax("f")

  override val autoSession = AutoSession

  def find(id: Short)(implicit session: DBSession): Option[Festival] = {
    withSQL {
      select.from(Festival as f).where.eq(f.id, id)
    }.map(Festival(f.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[Festival] = {
    withSQL(select.from(Festival as f)).map(Festival(f.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(Festival as f)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[Festival] = {
    withSQL {
      select.from(Festival as f).where.append(where)
    }.map(Festival(f.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[Festival] = {
    withSQL {
      select.from(Festival as f).where.append(where)
    }.map(Festival(f.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(Festival as f).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    times: Short,
    theme: String,
    themeRoman: String,
    themeKana: String,
    thumbnailUrl: Option[String] = None)(implicit session: DBSession): Festival = {
    val generatedKey = withSQL {
      insert.into(Festival).columns(
        column.times,
        column.theme,
        column.themeRoman,
        column.themeKana,
        column.thumbnailUrl
      ).values(
        times,
        theme,
        themeRoman,
        themeKana,
        thumbnailUrl
      )
    }.updateAndReturnGeneratedKey.apply()

    Festival(
      id = generatedKey.toShort,
      times = times,
      theme = theme,
      themeRoman = themeRoman,
      themeKana = themeKana,
      thumbnailUrl = thumbnailUrl)
  }

  def batchInsert(entities: Seq[Festival])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'times -> entity.times,
        'theme -> entity.theme,
        'themeRoman -> entity.themeRoman,
        'themeKana -> entity.themeKana,
        'thumbnailUrl -> entity.thumbnailUrl))
        SQL("""insert into festivals(
        times,
        theme,
        theme_roman,
        theme_kana,
        thumbnail_url
      ) values (
        {times},
        {theme},
        {themeRoman},
        {themeKana},
        {thumbnailUrl}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: Festival)(implicit session: DBSession): Festival = {
    withSQL {
      update(Festival).set(
        column.id -> entity.id,
        column.times -> entity.times,
        column.theme -> entity.theme,
        column.themeRoman -> entity.themeRoman,
        column.themeKana -> entity.themeKana,
        column.thumbnailUrl -> entity.thumbnailUrl
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Festival)(implicit session: DBSession): Unit = {
    withSQL { delete.from(Festival).where.eq(column.id, entity.id) }.update.apply()
  }

}
