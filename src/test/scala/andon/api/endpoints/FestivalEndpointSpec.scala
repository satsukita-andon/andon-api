package andon.api.endpoints

import io.circe.parse.parse
import com.twitter.finagle.http._

class FestivalEndpointSpec extends AndonServiceSuite {

  "GET /dev/festivals" should "return festivals" in { f =>
    val req = Request("/dev/festivals")
    val res = f(req)
    assert(res.statusCode == 200)
    val json = parse(res.contentString).toOption.get
    val theme60th = json.cursor.downAt(
      _.cursor.downField("times").get.focus.asNumber.get == 60
    ).get.field("theme").get.focus.asString.get
    assert(theme60th == "瞬")
  }

  "GET /dev/festivals" should "support sort order" in { f =>
    val req = Request("/dev/festivals?order=ASC")
    val res = f(req)
    assert(res.statusCode == 200)
    val json = parse(res.contentString).toOption.get
    val times = json.cursor.lefts.get.map(_.cursor.downField("times").get.focus.asNumber.get)
    println(times)
    println(json.cursor.rights.get.map(_.cursor.downField("times").get.focus.asNumber.get))
  }
}
