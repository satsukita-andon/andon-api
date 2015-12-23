name := "andon-api"

organization := "com.satsukita-andon"

version := "0.0.0"

scalaVersion := "2.11.7"

// from non/cats
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-language:postfixOps",
  "-Xfatal-warnings",
  "-Xlint",
  "-Xfuture",
  "-Yinline-warnings",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  // "-Ywarn-value-discard", generated code from scalikejdbcGen
  "-Ywarn-unused",
  "-Ywarn-unused-import"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  "jitpack" at "https://jitpack.io",
  "twttr" at "https://maven.twttr.com/"
)

libraryDependencies ++= {
  val twitterServer = "1.16.0"
  val finch = "0.9.3"
  val circe = "0.2.1"
  val scalikejdbc = "2.3.2"
  val postgres = "9.4-1206-jdbc42"
  val hikaricp = "2.4.3"
  val scalatest = "2.2.4"
  val jwt = "0.4.1"
  val joda = "2.9.1"
  val shapeless = "2.2.5"
  val cats = "0.3.0"
  val config = "1.3.0"
  Seq(
    "com.twitter" %% "twitter-server" % twitterServer,
    "com.github.finagle" %% "finch-core" % finch,
    "com.github.finagle" %% "finch-circe" % finch,
    "com.github.finagle" %% "finch-test" % finch % "test",
    "io.circe" %% "circe-core" % circe,
    "io.circe" %% "circe-generic" % circe,
    "io.circe" %% "circe-parse" % circe,
    "com.pauldijou" %% "jwt-core" % jwt,
    "org.scalikejdbc" %% "scalikejdbc" % scalikejdbc,
    "org.scalikejdbc" %% "scalikejdbc-test" % scalikejdbc % "test",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "org.postgresql" % "postgresql" % postgres,
    "com.zaxxer" % "HikariCP" % hikaricp,
    "org.scalatest" %% "scalatest" % scalatest % "test",
    "joda-time" % "joda-time" % joda,
    "com.chuusai" %% "shapeless" % shapeless,
    "org.spire-math" %% "cats" % cats,
    "com.typesafe" % "config" % config,
    "com.github.jeremyh" % "jBCrypt" % "jbcrypt-0.4"
  )
}

// db migration using flyway

seq(flywaySettings:  _*)

import com.typesafe.config.ConfigFactory

val conf = ConfigFactory.parseFile(new File("src/main/resources/application.conf"))
// val conf = ConfigFactory.parseFile(new File("src/test/resources/application.conf"))

flywayUrl := conf.getString("db.default.url")

flywayUser := conf.getString("db.default.user")

// generate models

scalikejdbcSettings

parallelExecution in Test := false
