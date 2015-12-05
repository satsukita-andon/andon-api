package andon.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scalikejdbc.config._

object Main extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executor = system.dispatcher

  val version = "dev"
  val host = "localhost"
  val port = 6039 // TODO: Make it possible to specify it by config file

  DBs.setupAll()

  Http().bindAndHandle(
    interface = host,
    port = port,
    handler = Routes.route(version)
  )
}
