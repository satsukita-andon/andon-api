package andon.api
package endpoints

import org.scalatest._

class AllEndpointSpec extends Suites(
  new FestivalEndpointSpec
) with BeforeAndAfterAll {
  override def beforeAll() = {
    println(s"${"=" * 40} startup ${"=" * 40}")
    DBSettings.setup()
  }
  override def afterAll() = {
    println(s"${"=" * 40} shutdown ${"=" * 40}")
    DBSettings.shutdown()
  }
}
