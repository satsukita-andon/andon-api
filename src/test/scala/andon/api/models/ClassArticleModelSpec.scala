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
    FestivalModel.create(classId.times, "", "", "")
    val c = ClassModel.create(classId, "はくえん").toOption.get
    val u = UserModel.findByLogin("admin").get
    create(u.id, c.id, PublishingStatus.Published, "hoge", "fuga", "comment")
    create(u.id, c.id, PublishingStatus.Private, "hoge2", "fuga", "comment")
    val asc = findAll(c.id, Paging().defaultOrder(ca.id -> ASC))
    assert(asc.nonEmpty)
    val asc2 = findAll(classId, Paging().defaultOrder(ca.id -> ASC))
    assert(asc == asc2)
    val desc = findAll(c.id, Paging().defaultOrder(ca.id -> DESC))
    assert(desc.reverse == asc)
    val offset10000 = findAll(c.id, Paging(offset = Some(10000)))
    assert(offset10000.isEmpty)
    val limit0 = findAll(c.id, Paging(limit = Some(0)))
    assert(limit0.isEmpty)
  }
}
