package andon.api.models.generated

import scalikejdbc._

case class Prize(
  id: Short,
  code: String,
  label: String,
  index: Short,
  color: String) {

  def save()(implicit session: DBSession): Prize = Prize.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = Prize.destroy(this)(session)

}


object Prize extends SQLSyntaxSupport[Prize] {

  override val tableName = "prizes"

  override val columns = Seq("id", "code", "label", "index", "color")

  def apply(p: SyntaxProvider[Prize])(rs: WrappedResultSet): Prize = apply(p.resultName)(rs)
  def apply(p: ResultName[Prize])(rs: WrappedResultSet): Prize = new Prize(
    id = rs.get(p.id),
    code = rs.get(p.code),
    label = rs.get(p.label),
    index = rs.get(p.index),
    color = rs.get(p.color)
  )

  val p = Prize.syntax("p")

  override val autoSession = AutoSession

  def find(id: Short)(implicit session: DBSession): Option[Prize] = {
    withSQL {
      select.from(Prize as p).where.eq(p.id, id)
    }.map(Prize(p.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[Prize] = {
    withSQL(select.from(Prize as p)).map(Prize(p.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(Prize as p)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[Prize] = {
    withSQL {
      select.from(Prize as p).where.append(where)
    }.map(Prize(p.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[Prize] = {
    withSQL {
      select.from(Prize as p).where.append(where)
    }.map(Prize(p.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(Prize as p).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    code: String,
    label: String,
    index: Short,
    color: String)(implicit session: DBSession): Prize = {
    val generatedKey = withSQL {
      insert.into(Prize).columns(
        column.code,
        column.label,
        column.index,
        column.color
      ).values(
        code,
        label,
        index,
        color
      )
    }.updateAndReturnGeneratedKey.apply()

    Prize(
      id = generatedKey.toShort,
      code = code,
      label = label,
      index = index,
      color = color)
  }

  def batchInsert(entities: Seq[Prize])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'code -> entity.code,
        'label -> entity.label,
        'index -> entity.index,
        'color -> entity.color))
        SQL("""insert into prizes(
        code,
        label,
        index,
        color
      ) values (
        {code},
        {label},
        {index},
        {color}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: Prize)(implicit session: DBSession): Prize = {
    withSQL {
      update(Prize).set(
        column.id -> entity.id,
        column.code -> entity.code,
        column.label -> entity.label,
        column.index -> entity.index,
        column.color -> entity.color
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Prize)(implicit session: DBSession): Unit = {
    withSQL { delete.from(Prize).where.eq(column.id, entity.id) }.update.apply()
  }

}
