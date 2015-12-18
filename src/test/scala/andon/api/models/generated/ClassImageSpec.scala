package andon.api.models.generated

import org.scalatest._
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc._
import org.joda.time.{DateTime}


class ClassImageSpec extends fixture.FlatSpec with Matchers with AutoRollback {
  val ci = ClassImage.syntax("ci")

  behavior of "ClassImage"

  it should "find by primary keys" in { implicit session =>
    val maybeFound = ClassImage.find(123)
    maybeFound.isDefined should be(true)
  }
  it should "find by where clauses" in { implicit session =>
    val maybeFound = ClassImage.findBy(sqls.eq(ci.id, 123))
    maybeFound.isDefined should be(true)
  }
  it should "find all records" in { implicit session =>
    val allResults = ClassImage.findAll()
    allResults.size should be >(0)
  }
  it should "count all records" in { implicit session =>
    val count = ClassImage.countAll()
    count should be >(0L)
  }
  it should "find all by where clauses" in { implicit session =>
    val results = ClassImage.findAllBy(sqls.eq(ci.id, 123))
    results.size should be >(0)
  }
  it should "count by where clauses" in { implicit session =>
    val count = ClassImage.countBy(sqls.eq(ci.id, 123))
    count should be >(0L)
  }
  it should "create new record" in { implicit session =>
    val created = ClassImage.create(classId = 123, userId = 123, url = "MyString", createdAt = DateTime.now)
    created should not be(null)
  }
  it should "save a record" in { implicit session =>
    val entity = ClassImage.findAll().head
    // TODO modify something
    val modified = entity
    val updated = ClassImage.save(modified)
    updated should not equal(entity)
  }
  it should "destroy a record" in { implicit session =>
    val entity = ClassImage.findAll().head
    ClassImage.destroy(entity)
    val shouldBeNone = ClassImage.find(123)
    shouldBeNone.isDefined should be(false)
  }
  it should "perform batch insert" in { implicit session =>
    val entities = ClassImage.findAll()
    entities.foreach(e => ClassImage.destroy(e))
    val batchInserted = ClassImage.batchInsert(entities)
    batchInserted.size should be >(0)
  }
}
