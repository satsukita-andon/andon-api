package andon.api

import com.typesafe.config.ConfigFactory
import com.twitter.finagle.Http
import com.twitter.util.Await

// for encode/decode json.
// if these are omited, an error occured at endpoints.all.toService
import io.finch.circe._
import io.circe.generic.auto._
import andon.api.endpoints.encodeException
import andon.api.jsons.Implicits._

object Main extends App {

  val conf = ConfigFactory.load()
  val port = conf.getInt("port")

  val settings = scalikejdbc.LoggingSQLAndTimeSettings(
    stackTraceDepth = 1
  )
  DBSettings.setup(settings)

  val service = endpoints.all.toService
  Await.ready(Http.server.serve(s":${port}", service))
}
