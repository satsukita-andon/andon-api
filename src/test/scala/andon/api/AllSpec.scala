package andon.api

import org.scalatest._
import scalikejdbc._
import org.joda.time.DateTime

import andon.api.endpoints._
import andon.api.models._
import andon.api.models.generated._
import andon.api.util.PasswordUtil

class AllSpec extends Suites(
  // model specs
  new ClassArticleModelSpec,
  // endpoint specs
  new FestivalEndpointSpec
) with BeforeAndAfterAll {
  override def beforeAll() = {
    println(s"${"=" * 30} startup all db-using specs ${"=" * 30}")
    val settings = LoggingSQLAndTimeSettings(
      stackTraceDepth = 1
    )
    DBSettings.setup(settings)
    initialize()
  }
  override def afterAll() = {
    println(s"${"=" * 30} shutdown all db-using specs ${"=" * 30}")
    clean()
    DBSettings.shutdown()
  }

  def initialize(): Unit = {
    DB.localTx { implicit s =>
      val now = DateTime.now
      UserModel.findByLogin("admin").getOrElse {
        User.create(
          login = "admin",
          password = PasswordUtil.encrypt("admin"),
          name = "管理人",
          times = 60,
          admin = true,
          suspended = false,
          createdAt = now,
          updatedAt = now
        )
      }
      UserModel.findByLogin("normal").getOrElse {
        User.create(
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
        )
      }

      UserModel.findByLogin("suspended").getOrElse {
        User.create(
          login = "suspended",
          password = PasswordUtil.encrypt("suspended"),
          name = "凍結ユーザー",
          times = 60,
          admin = false,
          suspended = true,
          createdAt = now,
          updatedAt = now
        )
      }
    }
  }

  def clean(): Unit = {
    DB.localTx { implicit s =>
      User.findAll.foreach(_.destroy)
      Festival.findAll.foreach(_.destroy)
      ClassArticle.findAll.foreach(_.destroy)
    }
  }
}
