package andon.api.endpoints

import io.finch.circe._
import io.circe._, generic.auto._

import com.twitter.finagle.http._
import io.finch.test._
import org.scalatest.fixture.FlatSpec

import andon.api.jsons.Implicits._
import andon.api.DBSettings

class FestivalEndpointSpec extends FlatSpec with ServiceSuite {

  def createService = all.toService

  "GET /dev/festivals" should "return festivals" in { f =>
    val req = Request("/dev/festivals")
    val res = f(req)
    println(req)
    println(res)
    println(res.contentString)
    assert(res.statusCode == 200)
  }
}
