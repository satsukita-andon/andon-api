package andon.api.models.generated

import org.scalatest._
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc._


class PrizeSpec extends fixture.FlatSpec with Matchers with AutoRollback {
  val p = Prize.syntax("p")

  behavior of "Prize"

  it should "find by primary keys" in { implicit session =>
    val maybeFound = Prize.find(123)
    maybeFound.isDefined should be(true)
  }
  it should "find by where clauses" in { implicit session =>
    val maybeFound = Prize.findBy(sqls.eq(p.id, 123))
    maybeFound.isDefined should be(true)
  }
  it should "find all records" in { implicit session =>
    val allResults = Prize.findAll()
    allResults.size should be >(0)
  }
  it should "count all records" in { implicit session =>
    val count = Prize.countAll()
    count should be >(0L)
  }
  it should "find all by where clauses" in { implicit session =>
    val results = Prize.findAllBy(sqls.eq(p.id, 123))
    results.size should be >(0)
  }
  it should "count by where clauses" in { implicit session =>
    val count = Prize.countBy(sqls.eq(p.id, 123))
    count should be >(0L)
  }
  it should "create new record" in { implicit session =>
    val created = Prize.create(code = "MyString", label = "MyString", index = 123, color = "MyString")
    created should not be(null)
  }
  it should "save a record" in { implicit session =>
    val entity = Prize.findAll().head
    // TODO modify something
    val modified = entity
    val updated = Prize.save(modified)
    updated should not equal(entity)
  }
  it should "destroy a record" in { implicit session =>
    val entity = Prize.findAll().head
    Prize.destroy(entity)
    val shouldBeNone = Prize.find(123)
    shouldBeNone.isDefined should be(false)
  }
  it should "perform batch insert" in { implicit session =>
    val entities = Prize.findAll()
    entities.foreach(e => Prize.destroy(e))
    val batchInserted = Prize.batchInsert(entities)
    batchInserted.size should be >(0)
  }
}
