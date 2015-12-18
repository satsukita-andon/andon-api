package andon.api.models.generated

import org.scalatest._
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc._
import org.joda.time.{DateTime}


class SchemaVersionSpec extends fixture.FlatSpec with Matchers with AutoRollback {
  val sv = SchemaVersion.syntax("sv")

  behavior of "SchemaVersion"

  it should "find by primary keys" in { implicit session =>
    val maybeFound = SchemaVersion.find("MyString")
    maybeFound.isDefined should be(true)
  }
  it should "find by where clauses" in { implicit session =>
    val maybeFound = SchemaVersion.findBy(sqls.eq(sv.version, "MyString"))
    maybeFound.isDefined should be(true)
  }
  it should "find all records" in { implicit session =>
    val allResults = SchemaVersion.findAll()
    allResults.size should be >(0)
  }
  it should "count all records" in { implicit session =>
    val count = SchemaVersion.countAll()
    count should be >(0L)
  }
  it should "find all by where clauses" in { implicit session =>
    val results = SchemaVersion.findAllBy(sqls.eq(sv.version, "MyString"))
    results.size should be >(0)
  }
  it should "count by where clauses" in { implicit session =>
    val count = SchemaVersion.countBy(sqls.eq(sv.version, "MyString"))
    count should be >(0L)
  }
  it should "create new record" in { implicit session =>
    val created = SchemaVersion.create(versionRank = 123, installedRank = 123, version = "MyString", description = "MyString", `type` = "MyString", script = "MyString", installedBy = "MyString", installedOn = DateTime.now, executionTime = 123, success = false)
    created should not be(null)
  }
  it should "save a record" in { implicit session =>
    val entity = SchemaVersion.findAll().head
    // TODO modify something
    val modified = entity
    val updated = SchemaVersion.save(modified)
    updated should not equal(entity)
  }
  it should "destroy a record" in { implicit session =>
    val entity = SchemaVersion.findAll().head
    SchemaVersion.destroy(entity)
    val shouldBeNone = SchemaVersion.find("MyString")
    shouldBeNone.isDefined should be(false)
  }
  it should "perform batch insert" in { implicit session =>
    val entities = SchemaVersion.findAll()
    entities.foreach(e => SchemaVersion.destroy(e))
    val batchInserted = SchemaVersion.batchInsert(entities)
    batchInserted.size should be >(0)
  }
}
