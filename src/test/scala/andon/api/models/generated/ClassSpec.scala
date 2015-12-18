package andon.api.models.generated

import org.scalatest._
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc._
import org.joda.time.{DateTime}


class ClassSpec extends fixture.FlatSpec with Matchers with AutoRollback {
  val c = Class.syntax("c")

  behavior of "Class"

  it should "find by primary keys" in { implicit session =>
    val maybeFound = Class.find(123)
    maybeFound.isDefined should be(true)
  }
  it should "find by where clauses" in { implicit session =>
    val maybeFound = Class.findBy(sqls.eq(c.id, 123))
    maybeFound.isDefined should be(true)
  }
  it should "find all records" in { implicit session =>
    val allResults = Class.findAll()
    allResults.size should be >(0)
  }
  it should "count all records" in { implicit session =>
    val count = Class.countAll()
    count should be >(0L)
  }
  it should "find all by where clauses" in { implicit session =>
    val results = Class.findAllBy(sqls.eq(c.id, 123))
    results.size should be >(0)
  }
  it should "count by where clauses" in { implicit session =>
    val count = Class.countBy(sqls.eq(c.id, 123))
    count should be >(0L)
  }
  it should "create new record" in { implicit session =>
    val created = Class.create(times = 123, grade = 123, `class` = 123, title = "MyString", createdAt = DateTime.now, updatedAt = DateTime.now)
    created should not be(null)
  }
  it should "save a record" in { implicit session =>
    val entity = Class.findAll().head
    // TODO modify something
    val modified = entity
    val updated = Class.save(modified)
    updated should not equal(entity)
  }
  it should "destroy a record" in { implicit session =>
    val entity = Class.findAll().head
    Class.destroy(entity)
    val shouldBeNone = Class.find(123)
    shouldBeNone.isDefined should be(false)
  }
  it should "perform batch insert" in { implicit session =>
    val entities = Class.findAll()
    entities.foreach(e => Class.destroy(e))
    val batchInserted = Class.batchInsert(entities)
    batchInserted.size should be >(0)
  }
}
