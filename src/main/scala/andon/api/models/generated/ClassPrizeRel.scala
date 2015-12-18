package andon.api.models.generated

import scalikejdbc._

case class ClassPrizeRel(
  id: Short,
  classId: Short,
  prizeId: Short) {

  def save()(implicit session: DBSession): ClassPrizeRel = ClassPrizeRel.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = ClassPrizeRel.destroy(this)(session)

}


object ClassPrizeRel extends SQLSyntaxSupport[ClassPrizeRel] {

  override val tableName = "class_prize_rel"

  override val columns = Seq("id", "class_id", "prize_id")

  def apply(cpr: SyntaxProvider[ClassPrizeRel])(rs: WrappedResultSet): ClassPrizeRel = apply(cpr.resultName)(rs)
  def apply(cpr: ResultName[ClassPrizeRel])(rs: WrappedResultSet): ClassPrizeRel = new ClassPrizeRel(
    id = rs.get(cpr.id),
    classId = rs.get(cpr.classId),
    prizeId = rs.get(cpr.prizeId)
  )

  val cpr = ClassPrizeRel.syntax("cpr")

  override val autoSession = AutoSession

  def find(id: Short)(implicit session: DBSession): Option[ClassPrizeRel] = {
    withSQL {
      select.from(ClassPrizeRel as cpr).where.eq(cpr.id, id)
    }.map(ClassPrizeRel(cpr.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ClassPrizeRel] = {
    withSQL(select.from(ClassPrizeRel as cpr)).map(ClassPrizeRel(cpr.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ClassPrizeRel as cpr)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ClassPrizeRel] = {
    withSQL {
      select.from(ClassPrizeRel as cpr).where.append(where)
    }.map(ClassPrizeRel(cpr.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ClassPrizeRel] = {
    withSQL {
      select.from(ClassPrizeRel as cpr).where.append(where)
    }.map(ClassPrizeRel(cpr.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ClassPrizeRel as cpr).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    classId: Short,
    prizeId: Short)(implicit session: DBSession): ClassPrizeRel = {
    val generatedKey = withSQL {
      insert.into(ClassPrizeRel).columns(
        column.classId,
        column.prizeId
      ).values(
        classId,
        prizeId
      )
    }.updateAndReturnGeneratedKey.apply()

    ClassPrizeRel(
      id = generatedKey.toShort,
      classId = classId,
      prizeId = prizeId)
  }

  def batchInsert(entities: Seq[ClassPrizeRel])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'classId -> entity.classId,
        'prizeId -> entity.prizeId))
        SQL("""insert into class_prize_rel(
        class_id,
        prize_id
      ) values (
        {classId},
        {prizeId}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: ClassPrizeRel)(implicit session: DBSession): ClassPrizeRel = {
    withSQL {
      update(ClassPrizeRel).set(
        column.id -> entity.id,
        column.classId -> entity.classId,
        column.prizeId -> entity.prizeId
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ClassPrizeRel)(implicit session: DBSession): Unit = {
    withSQL { delete.from(ClassPrizeRel).where.eq(column.id, entity.id) }.update.apply()
  }

}
