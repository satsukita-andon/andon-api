package andon.api.models.generated

import org.scalatest._
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc._


class FestivalSpec extends fixture.FlatSpec with Matchers with AutoRollback {
  val f = Festival.syntax("f")

  behavior of "Festival"

  it should "find by primary keys" in { implicit session =>
    val maybeFound = Festival.find(123)
    maybeFound.isDefined should be(true)
  }
  it should "find by where clauses" in { implicit session =>
    val maybeFound = Festival.findBy(sqls.eq(f.id, 123))
    maybeFound.isDefined should be(true)
  }
  it should "find all records" in { implicit session =>
    val allResults = Festival.findAll()
    allResults.size should be >(0)
  }
  it should "count all records" in { implicit session =>
    val count = Festival.countAll()
    count should be >(0L)
  }
  it should "find all by where clauses" in { implicit session =>
    val results = Festival.findAllBy(sqls.eq(f.id, 123))
    results.size should be >(0)
  }
  it should "count by where clauses" in { implicit session =>
    val count = Festival.countBy(sqls.eq(f.id, 123))
    count should be >(0L)
  }
  it should "create new record" in { implicit session =>
    val created = Festival.create(times = 123, theme = "MyString", themeRoman = "MyString", themeKana = "MyString")
    created should not be(null)
  }
  it should "save a record" in { implicit session =>
    val entity = Festival.findAll().head
    // TODO modify something
    val modified = entity
    val updated = Festival.save(modified)
    updated should not equal(entity)
  }
  it should "destroy a record" in { implicit session =>
    val entity = Festival.findAll().head
    Festival.destroy(entity)
    val shouldBeNone = Festival.find(123)
    shouldBeNone.isDefined should be(false)
  }
  it should "perform batch insert" in { implicit session =>
    val entities = Festival.findAll()
    entities.foreach(e => Festival.destroy(e))
    val batchInserted = Festival.batchInsert(entities)
    batchInserted.size should be >(0)
  }
}
