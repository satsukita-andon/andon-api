package andon.api.models.generated

import org.scalatest._
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc._


class ClassTagSpec extends fixture.FlatSpec with Matchers with AutoRollback {
  val ct = ClassTag.syntax("ct")

  behavior of "ClassTag"

  it should "find by primary keys" in { implicit session =>
    val maybeFound = ClassTag.find(123)
    maybeFound.isDefined should be(true)
  }
  it should "find by where clauses" in { implicit session =>
    val maybeFound = ClassTag.findBy(sqls.eq(ct.id, 123))
    maybeFound.isDefined should be(true)
  }
  it should "find all records" in { implicit session =>
    val allResults = ClassTag.findAll()
    allResults.size should be >(0)
  }
  it should "count all records" in { implicit session =>
    val count = ClassTag.countAll()
    count should be >(0L)
  }
  it should "find all by where clauses" in { implicit session =>
    val results = ClassTag.findAllBy(sqls.eq(ct.id, 123))
    results.size should be >(0)
  }
  it should "count by where clauses" in { implicit session =>
    val count = ClassTag.countBy(sqls.eq(ct.id, 123))
    count should be >(0L)
  }
  it should "create new record" in { implicit session =>
    val created = ClassTag.create(classId = 123, label = "MyString")
    created should not be(null)
  }
  it should "save a record" in { implicit session =>
    val entity = ClassTag.findAll().head
    // TODO modify something
    val modified = entity
    val updated = ClassTag.save(modified)
    updated should not equal(entity)
  }
  it should "destroy a record" in { implicit session =>
    val entity = ClassTag.findAll().head
    ClassTag.destroy(entity)
    val shouldBeNone = ClassTag.find(123)
    shouldBeNone.isDefined should be(false)
  }
  it should "perform batch insert" in { implicit session =>
    val entities = ClassTag.findAll()
    entities.foreach(e => ClassTag.destroy(e))
    val batchInserted = ClassTag.batchInsert(entities)
    batchInserted.size should be >(0)
  }
}
