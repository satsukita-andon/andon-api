package andon.api.endpoints

import scala.language.postfixOps
import org.scalatest._
import io.circe.parse.parse
import com.twitter.finagle.http._

@DoNotDiscover
class FestivalEndpointSpec extends AndonServiceSuite with JsonUtil {

  "GET /dev/festivals" should "return festivals" in { f =>
    val req = Request("/dev/festivals")
    val res = f(req)
    assert(res.statusCode == 200)
    val json = parse(res.contentString).toOption.get
    assert(json.t ? { _.t / "times" == 60 } / "theme" == "瞬")
  }

  it should "support sort order" in { f =>
    val req = Request("/dev/festivals?order=ASC")
    val res = f(req)
    assert(res.statusCode == 200)
    val json = parse(res.contentString).toOption.get
    val times = json.t.map { _.t / "times" asLong }
    assert(times.sorted == times)
  }
}
