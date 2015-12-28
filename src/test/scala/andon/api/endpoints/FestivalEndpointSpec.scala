package andon.api.endpoints

import org.scalatest._
import io.circe.parse.parse
import com.twitter.finagle.http._
import com.twitter.io.Buf

import andon.api.jsons.FestivalCreation

@DoNotDiscover
class FestivalEndpointSpec extends AndonServiceSuite with AndonUtil with JsonUtil {

  "GET /dev/festivals" should "return festivals" in { f =>
    withFestivals(Seq(
      FestivalCreation(62, "?", "?", "?", None),
      FestivalCreation(60, "瞬", "matataki", "またたき", None),
      FestivalCreation(61, "?", "?", "?", None)
    )) { _ =>
      val req = Request("/dev/festivals")
      val res = f(req)
      assert(res.statusCode == 200)
      val json = parse(res.contentString).toOption.get
      assert(json.t./("items").? { _.t / "times" == 60 } / "theme" == "瞬")
      assert(json.t./("items").map { _.t / "times" asLong } == Seq(62, 61, 60), "default order should be DESC")
    }
  }

  it should "support sort order" in { f =>
    withFestivals(Seq(
      FestivalCreation(62, "?", "?", "?", None),
      FestivalCreation(60, "瞬", "matataki", "またたき", None),
      FestivalCreation(61, "?", "?", "?", None)
    )) { _ =>
      val req = Request("/dev/festivals?order=ASC")
      val res = f(req)
      assert(res.statusCode == 200)
      val json = parse(res.contentString).toOption.get
      val times = json.t./("items").map { _.t / "times" asLong }
      assert(times == Seq(60, 61, 62))
    }
  }

  "POST /dev/festivals" should "create new festival" in { f =>
    val token = tokenOf("admin")
    val req = RequestBuilder()
      .url("http://localhost/dev/festivals")
      .setHeader("Authorization", s"Bearer ${token}")
      .buildPost(Buf.Utf8("""
{
  "times": 100,
  "theme": "未来",
  "theme_roman": "mirai",
  "theme_kana": "みらい"
}
"""))
    val res = f(req)
    assert(res.statusCode == 200, res.contentString)
    val req2 = Request("/dev/festivals")
    val res2 = f(req2)
    assert(res2.statusCode == 200)
    val json = parse(res2.contentString).toOption.get
    assert(json.t./("items").map { _.t / "times" asLong }.contains(100))
  }
}
