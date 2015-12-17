package andon.api.models.generated

import org.scalatest._
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc._
import org.joda.time.{DateTime}


class ArticleSpec extends fixture.FlatSpec with Matchers with AutoRollback {
  val a = Article.syntax("a")

  behavior of "Article"

  it should "find by primary keys" in { implicit session =>
    val maybeFound = Article.find(123)
    maybeFound.isDefined should be(true)
  }
  it should "find by where clauses" in { implicit session =>
    val maybeFound = Article.findBy(sqls.eq(a.id, 123))
    maybeFound.isDefined should be(true)
  }
  it should "find all records" in { implicit session =>
    val allResults = Article.findAll()
    allResults.size should be >(0)
  }
  it should "count all records" in { implicit session =>
    val count = Article.countAll()
    count should be >(0L)
  }
  it should "find all by where clauses" in { implicit session =>
    val results = Article.findAllBy(sqls.eq(a.id, 123))
    results.size should be >(0)
  }
  it should "count by where clauses" in { implicit session =>
    val count = Article.countBy(sqls.eq(a.id, 123))
    count should be >(0L)
  }
  it should "create new record" in { implicit session =>
    val created = Article.create(ownerId = 123, latestRevisionNumber = 123, status = "MyString", editorialRight = "MyString", createdAt = DateTime.now, updatedAt = DateTime.now)
    created should not be(null)
  }
  it should "save a record" in { implicit session =>
    val entity = Article.findAll().head
    // TODO modify something
    val modified = entity
    val updated = Article.save(modified)
    updated should not equal(entity)
  }
  it should "destroy a record" in { implicit session =>
    val entity = Article.findAll().head
    Article.destroy(entity)
    val shouldBeNone = Article.find(123)
    shouldBeNone.isDefined should be(false)
  }
  it should "perform batch insert" in { implicit session =>
    val entities = Article.findAll()
    entities.foreach(e => Article.destroy(e))
    val batchInserted = Article.batchInsert(entities)
    batchInserted.size should be >(0)
  }
}
