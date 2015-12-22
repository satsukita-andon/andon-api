package andon.api

import scala.concurrent.SyncVar
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.{ HikariDataSource, HikariConfig }
import scalikejdbc._

object DBSettings {

  var dataSource: HikariDataSource = null
  var numberOfUsers: Int = 0

  def newDataSource = {
    val conf = ConfigFactory.load()
    val config = new HikariConfig()
    config.setJdbcUrl(conf.getString("db.default.url"))
    config.setDriverClassName(conf.getString("db.default.driver"))
    config.setUsername(conf.getString("db.default.user"))
    config.setPassword(conf.getString("db.default.password"))
    config.addDataSourceProperty("stringtype", "unspecified") // workaround: http://stackoverflow.com/questions/14719207/how-do-you-insert-a-postgres-enum-value-using-clojure-jdbc
    new HikariDataSource(config)
  }

  def setup(s: LoggingSQLAndTimeSettings = LoggingSQLAndTimeSettings()): Unit = {
    synchronized {
      if (numberOfUsers == 0) {
        dataSource = newDataSource
        ConnectionPool.singleton(new DataSourceConnectionPool(dataSource))
        numberOfUsers = 1
      } else {
        numberOfUsers += 1
      }
      GlobalSettings.loggingSQLAndTime = s
    }
  }

  def shutdown(): Unit = {
    synchronized {
      if (numberOfUsers <= 0) {
        dataSource.close()
        throw new Exception("Who does shutdown twice?")
      } else if (numberOfUsers == 1) {
        dataSource.close()
        numberOfUsers = 0
      } else {
        numberOfUsers -= 1
      }
    }
  }
}
