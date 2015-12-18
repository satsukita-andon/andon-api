package andon.api.models.generated

import scalikejdbc._

case class ClassTag(
  id: Int,
  classId: Short,
  label: String) {

  def save()(implicit session: DBSession): ClassTag = ClassTag.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = ClassTag.destroy(this)(session)

}


object ClassTag extends SQLSyntaxSupport[ClassTag] {

  override val tableName = "class_tags"

  override val columns = Seq("id", "class_id", "label")

  def apply(ct: SyntaxProvider[ClassTag])(rs: WrappedResultSet): ClassTag = apply(ct.resultName)(rs)
  def apply(ct: ResultName[ClassTag])(rs: WrappedResultSet): ClassTag = new ClassTag(
    id = rs.get(ct.id),
    classId = rs.get(ct.classId),
    label = rs.get(ct.label)
  )

  val ct = ClassTag.syntax("ct")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[ClassTag] = {
    withSQL {
      select.from(ClassTag as ct).where.eq(ct.id, id)
    }.map(ClassTag(ct.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[ClassTag] = {
    withSQL(select.from(ClassTag as ct)).map(ClassTag(ct.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(ClassTag as ct)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[ClassTag] = {
    withSQL {
      select.from(ClassTag as ct).where.append(where)
    }.map(ClassTag(ct.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[ClassTag] = {
    withSQL {
      select.from(ClassTag as ct).where.append(where)
    }.map(ClassTag(ct.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(ClassTag as ct).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    classId: Short,
    label: String)(implicit session: DBSession): ClassTag = {
    val generatedKey = withSQL {
      insert.into(ClassTag).columns(
        column.classId,
        column.label
      ).values(
        classId,
        label
      )
    }.updateAndReturnGeneratedKey.apply()

    ClassTag(
      id = generatedKey.toInt,
      classId = classId,
      label = label)
  }

  def batchInsert(entities: Seq[ClassTag])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'classId -> entity.classId,
        'label -> entity.label))
        SQL("""insert into class_tags(
        class_id,
        label
      ) values (
        {classId},
        {label}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: ClassTag)(implicit session: DBSession): ClassTag = {
    withSQL {
      update(ClassTag).set(
        column.id -> entity.id,
        column.classId -> entity.classId,
        column.label -> entity.label
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ClassTag)(implicit session: DBSession): Unit = {
    withSQL { delete.from(ClassTag).where.eq(column.id, entity.id) }.update.apply()
  }

}
