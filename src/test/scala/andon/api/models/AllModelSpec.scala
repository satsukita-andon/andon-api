package andon.api
package models

import org.scalatest._
import scalikejdbc._

class AllModelSpec extends Suites(
  new ClassArticleModelSpec
) with BeforeAndAfterAll {
  override def beforeAll() = {
    println(s"${"=" * 30} startup model specs ${"=" * 30}")
    val settings = LoggingSQLAndTimeSettings(
      stackTraceDepth = 1
    )
    DBSettings.setup(settings)
  }
  override def afterAll() = {
    println(s"${"=" * 30} shutdown model specs ${"=" * 30}")
    DBSettings.shutdown()
  }
}
