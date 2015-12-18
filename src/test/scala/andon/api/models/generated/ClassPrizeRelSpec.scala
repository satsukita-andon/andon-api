package andon.api.models.generated

import org.scalatest._
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc._


class ClassPrizeRelSpec extends fixture.FlatSpec with Matchers with AutoRollback {
  val cpr = ClassPrizeRel.syntax("cpr")

  behavior of "ClassPrizeRel"

  it should "find by primary keys" in { implicit session =>
    val maybeFound = ClassPrizeRel.find(123)
    maybeFound.isDefined should be(true)
  }
  it should "find by where clauses" in { implicit session =>
    val maybeFound = ClassPrizeRel.findBy(sqls.eq(cpr.id, 123))
    maybeFound.isDefined should be(true)
  }
  it should "find all records" in { implicit session =>
    val allResults = ClassPrizeRel.findAll()
    allResults.size should be >(0)
  }
  it should "count all records" in { implicit session =>
    val count = ClassPrizeRel.countAll()
    count should be >(0L)
  }
  it should "find all by where clauses" in { implicit session =>
    val results = ClassPrizeRel.findAllBy(sqls.eq(cpr.id, 123))
    results.size should be >(0)
  }
  it should "count by where clauses" in { implicit session =>
    val count = ClassPrizeRel.countBy(sqls.eq(cpr.id, 123))
    count should be >(0L)
  }
  it should "create new record" in { implicit session =>
    val created = ClassPrizeRel.create(classId = 123, prizeId = 123)
    created should not be(null)
  }
  it should "save a record" in { implicit session =>
    val entity = ClassPrizeRel.findAll().head
    // TODO modify something
    val modified = entity
    val updated = ClassPrizeRel.save(modified)
    updated should not equal(entity)
  }
  it should "destroy a record" in { implicit session =>
    val entity = ClassPrizeRel.findAll().head
    ClassPrizeRel.destroy(entity)
    val shouldBeNone = ClassPrizeRel.find(123)
    shouldBeNone.isDefined should be(false)
  }
  it should "perform batch insert" in { implicit session =>
    val entities = ClassPrizeRel.findAll()
    entities.foreach(e => ClassPrizeRel.destroy(e))
    val batchInserted = ClassPrizeRel.batchInsert(entities)
    batchInserted.size should be >(0)
  }
}
