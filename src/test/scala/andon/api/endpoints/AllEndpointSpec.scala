package andon.api
package endpoints

import org.scalatest._
import scalikejdbc._
import org.joda.time.DateTime

import andon.api.models.generated.User
import andon.api.util.PasswordUtil

class AllEndpointSpec extends Suites(
  new FestivalEndpointSpec
) with BeforeAndAfterAll {
  override def beforeAll() = {
    println(s"${"=" * 40} startup ${"=" * 40}")
    val settings = LoggingSQLAndTimeSettings(
      stackTraceDepth = 1
    )
    DBSettings.setup(settings)
    insertTestUsers()
  }
  override def afterAll() = {
    println(s"${"=" * 40} shutdown ${"=" * 40}")
    deleteTestUsers()
    DBSettings.shutdown()
  }

  var admin: Option[User] = None
  var normal: Option[User] = None
  var suspended: Option[User] = None

  private def insertTestUsers(): Unit = {
    DB.localTx { implicit s =>
      val now = DateTime.now
      this.admin = Some(User.create(
        login = "admin",
        password = PasswordUtil.encrypt("admin"),
        name = "管理人",
        times = 60,
        admin = true,
        suspended = false,
        createdAt = now,
        updatedAt = now
      ))
      this.normal = Some(User.create(
        login = "normal",
        password = PasswordUtil.encrypt("normal"),
        name = "通常ユーザー",
        times = 60,
        classFirst = Some(5),
        classSecond = Some(7),
        classThird = Some(9),
        admin = false,
        suspended = false,
        createdAt = now,
        updatedAt = now
      ))
      this.suspended = Some(User.create(
        login = "suspended",
        password = PasswordUtil.encrypt("suspended"),
        name = "凍結ユーザー",
        times = 60,
        admin = false,
        suspended = true,
        createdAt = now,
        updatedAt = now
      ))
    }
  }

  private def deleteTestUsers(): Unit = {
    DB.localTx { implicit s =>
      admin.foreach(User.destroy)
      normal.foreach(User.destroy)
      suspended.foreach(User.destroy)
    }
  }
}
