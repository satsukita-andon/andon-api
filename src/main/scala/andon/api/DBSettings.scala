package andon.api

import scala.concurrent.SyncVar
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.{ HikariDataSource, HikariConfig }
import scalikejdbc._

object DBSettings {

  val dataSourceVar: SyncVar[HikariDataSource] = new SyncVar()

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
    val dataSource = dataSourceVar.get(0).getOrElse {
      val dataSource = newDataSource
      dataSourceVar.put(dataSource) // FIXME: maybe already set?
      dataSource
    }
    GlobalSettings.loggingSQLAndTime = s
    ConnectionPool.singleton(new DataSourceConnectionPool(dataSource))
  }

  def shutdown(): Unit = {
    dataSourceVar.get(0).foreach(_.close())
  }
}
