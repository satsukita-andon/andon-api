package andon.api.models

import scalikejdbc.scalatest.AutoRollback
import org.joda.time.DateTime
import org.scalatest._

import andon.api.util._
import ClassArticleModel._

@DoNotDiscover
class ClassArticleModelSpec extends fixture.FlatSpec with AutoRollback {
  "#findAll" should "be OK" in { implicit s =>
    val classId = ClassId(OrdInt(60), 3, 9)
    val id = ClassModel.findId(classId).get
    val asc = findAll(id, Paging(order = Some(ASC)))
    assert(asc.nonEmpty)
    val asc2 = findAll(classId, Paging(order = Some(ASC)))
    assert(asc == asc2)
    val desc = findAll(id, Paging(offset = Some(0), order = Some(DESC)))
    assert(desc.sortBy(_._1.id) == asc)
    val offset10000 = findAll(id, Paging(offset = Some(10000)))
    assert(offset10000.isEmpty)
    val limit0 = findAll(id, Paging(limit = Some(0)))
    assert(limit0.isEmpty)
  }
}
