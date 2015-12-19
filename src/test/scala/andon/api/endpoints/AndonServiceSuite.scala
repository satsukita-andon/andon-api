package andon.api.endpoints

import io.finch.circe._
import io.finch.test._
import io.circe._, generic.auto._
import org.scalatest.fixture.FlatSpec

import andon.api.jsons.Implicits._

trait AndonServiceSuite extends FlatSpec with ServiceSuite {
  def createService = all.toService
}
