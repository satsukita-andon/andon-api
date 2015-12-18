package andon.api.models.generated

import org.scalatest._
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc._
import org.joda.time.{DateTime}


class ClassArticleSpec extends fixture.FlatSpec with Matchers with AutoRollback {
  val ca = ClassArticle.syntax("ca")

  behavior of "ClassArticle"

  it should "find by primary keys" in { implicit session =>
    val maybeFound = ClassArticle.find(123)
    maybeFound.isDefined should be(true)
  }
  it should "find by where clauses" in { implicit session =>
    val maybeFound = ClassArticle.findBy(sqls.eq(ca.id, 123))
    maybeFound.isDefined should be(true)
  }
  it should "find all records" in { implicit session =>
    val allResults = ClassArticle.findAll()
    allResults.size should be >(0)
  }
  it should "count all records" in { implicit session =>
    val count = ClassArticle.countAll()
    count should be >(0L)
  }
  it should "find all by where clauses" in { implicit session =>
    val results = ClassArticle.findAllBy(sqls.eq(ca.id, 123))
    results.size should be >(0)
  }
  it should "count by where clauses" in { implicit session =>
    val count = ClassArticle.countBy(sqls.eq(ca.id, 123))
    count should be >(0L)
  }
  it should "create new record" in { implicit session =>
    val created = ClassArticle.create(classId = 123, latestRevisionNumber = 123, status = "MyString", createdAt = DateTime.now, updatedAt = DateTime.now)
    created should not be(null)
  }
  it should "save a record" in { implicit session =>
    val entity = ClassArticle.findAll().head
    // TODO modify something
    val modified = entity
    val updated = ClassArticle.save(modified)
    updated should not equal(entity)
  }
  it should "destroy a record" in { implicit session =>
    val entity = ClassArticle.findAll().head
    ClassArticle.destroy(entity)
    val shouldBeNone = ClassArticle.find(123)
    shouldBeNone.isDefined should be(false)
  }
  it should "perform batch insert" in { implicit session =>
    val entities = ClassArticle.findAll()
    entities.foreach(e => ClassArticle.destroy(e))
    val batchInserted = ClassArticle.batchInsert(entities)
    batchInserted.size should be >(0)
  }
}
