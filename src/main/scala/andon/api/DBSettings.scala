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
    config.setDataSourceClassName(conf.getString("db.default.dataSource"))
    config.addDataSourceProperty("serverName", conf.getString("db.default.serverName")) // hostname if you use remote host
    config.addDataSourceProperty("portNumber", conf.getNumber("db.default.postNumber")) // post if you use remote host
    config.addDataSourceProperty("databaseName", conf.getString("db.default.databaseName"))
    config.addDataSourceProperty("user", conf.getString("db.default.user"))
    config.addDataSourceProperty("password", conf.getString("db.default.password"))
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
