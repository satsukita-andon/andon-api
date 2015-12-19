package andon.api
package endpoints

import org.scalatest._
import scalikejdbc._

class AllEndpointSpec extends Suites(
  new FestivalEndpointSpec
) with BeforeAndAfterAll {
  override def beforeAll() = {
    println(s"${"=" * 40} startup ${"=" * 40}")
    val settings = LoggingSQLAndTimeSettings(
      stackTraceDepth = 1
    )
    DBSettings.setup(settings)
  }
  override def afterAll() = {
    println(s"${"=" * 40} shutdown ${"=" * 40}")
    DBSettings.shutdown()
  }
}
