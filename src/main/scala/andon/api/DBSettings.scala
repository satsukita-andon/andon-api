package andon.api

import com.typesafe.config.ConfigFactory
import javax.sql.DataSource
import scalikejdbc._
import com.zaxxer.hikari._

object DBSettings {
  def setup(): Unit = {
    GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
      stackTraceDepth = 1
    )
    val conf = ConfigFactory.load()
    val dataSource: DataSource = {
      val config = new HikariConfig()
      config.setDataSourceClassName(conf.getString("db.default.dataSource"))
      config.addDataSourceProperty("serverName", conf.getString("db.default.serverName")) // hostname if you use remote host
      config.addDataSourceProperty("portNumber", conf.getNumber("db.default.postNumber")) // post if you use remote host
      config.addDataSourceProperty("databaseName", conf.getString("db.default.databaseName"))
      config.addDataSourceProperty("user", conf.getString("db.default.user"))
      config.addDataSourceProperty("password", conf.getString("db.default.password"))
      new HikariDataSource(config);
    }
    ConnectionPool.singleton(new DataSourceConnectionPool(dataSource))
  }
}
