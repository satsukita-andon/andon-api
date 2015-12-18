package andon.api.models.generated

import org.scalatest._
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc._
import org.joda.time.{DateTime}


class ClassArticleRevisionSpec extends fixture.FlatSpec with Matchers with AutoRollback {
  val car = ClassArticleRevision.syntax("car")

  behavior of "ClassArticleRevision"

  it should "find by primary keys" in { implicit session =>
    val maybeFound = ClassArticleRevision.find(123)
    maybeFound.isDefined should be(true)
  }
  it should "find by where clauses" in { implicit session =>
    val maybeFound = ClassArticleRevision.findBy(sqls.eq(car.id, 123))
    maybeFound.isDefined should be(true)
  }
  it should "find all records" in { implicit session =>
    val allResults = ClassArticleRevision.findAll()
    allResults.size should be >(0)
  }
  it should "count all records" in { implicit session =>
    val count = ClassArticleRevision.countAll()
    count should be >(0L)
  }
  it should "find all by where clauses" in { implicit session =>
    val results = ClassArticleRevision.findAllBy(sqls.eq(car.id, 123))
    results.size should be >(0)
  }
  it should "count by where clauses" in { implicit session =>
    val count = ClassArticleRevision.countBy(sqls.eq(car.id, 123))
    count should be >(0L)
  }
  it should "create new record" in { implicit session =>
    val created = ClassArticleRevision.create(articleId = 123, revisionNumber = 123, title = "MyString", body = "MyString", comment = "MyString", createdAt = DateTime.now)
    created should not be(null)
  }
  it should "save a record" in { implicit session =>
    val entity = ClassArticleRevision.findAll().head
    // TODO modify something
    val modified = entity
    val updated = ClassArticleRevision.save(modified)
    updated should not equal(entity)
  }
  it should "destroy a record" in { implicit session =>
    val entity = ClassArticleRevision.findAll().head
    ClassArticleRevision.destroy(entity)
    val shouldBeNone = ClassArticleRevision.find(123)
    shouldBeNone.isDefined should be(false)
  }
  it should "perform batch insert" in { implicit session =>
    val entities = ClassArticleRevision.findAll()
    entities.foreach(e => ClassArticleRevision.destroy(e))
    val batchInserted = ClassArticleRevision.batchInsert(entities)
    batchInserted.size should be >(0)
  }
}
