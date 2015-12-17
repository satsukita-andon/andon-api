package andon.api

import com.twitter.finagle.Http
import com.twitter.util.Await
import scalikejdbc.config.DBs

// for encode/decode json.
// if these are omited, an error occured at endpoints.all.toService
import io.finch.circe._
import io.circe.generic.auto._

object Main extends App {

  val port = 6039

  DBs.setupAll()

  val service = endpoints.all.toService
  Await.ready(Http.server.serve(s":${port}", service))
}
