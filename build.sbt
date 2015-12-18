name := "andon-api"

organization := "com.satsukita-andon"

version := "0.0.0"

scalaVersion := "2.11.7"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlint",
  "-Xfatal-warnings",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused",
  "-Ywarn-unused-import"
  // "-Ywarn-value-discard"
)

resolvers ++= Seq(
  "jitpack" at "https://jitpack.io",
  "twttr" at "https://maven.twttr.com/"
)

libraryDependencies ++= {
  val twitterServer = "1.16.0"
  val finch = "0.9.2"
  val circe = "0.2.1"
  val scalikejdbc = "2.3.1"
  val scalatest = "2.2.4"
  val postgres = "9.4-1206-jdbc42"
  val jwt = "0.4.1"
  val joda = "2.9.1"
  val shapeless = "2.2.5"
  val cats = "0.3.0"
  Seq(
    "com.twitter" %% "twitter-server" % twitterServer,
    "com.github.finagle" %% "finch-core" % finch,
    "com.github.finagle" %% "finch-circe" % finch,
    "io.circe" %% "circe-core" % circe,
    "io.circe" %% "circe-generic" % circe,
    "io.circe" %% "circe-parse" % circe,
    "com.pauldijou" %% "jwt-core" % jwt,
    "org.scalikejdbc" %% "scalikejdbc" % scalikejdbc,
    "org.scalikejdbc" %% "scalikejdbc-config" % scalikejdbc,
    "org.scalikejdbc" %% "scalikejdbc-test" % scalikejdbc % "test",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "org.postgresql" % "postgresql" % postgres,
    "org.scalatest" %% "scalatest" % scalatest % "test",
    "joda-time" % "joda-time" % joda,
    "com.chuusai" %% "shapeless" % shapeless,
    "org.spire-math" %% "cats" % cats,
    "com.github.jeremyh" % "jBCrypt" % "jbcrypt-0.4"
  )
}

// db migration using flyway

seq(flywaySettings:  _*)

import com.typesafe.config.ConfigFactory

val conf = ConfigFactory.parseFile(new File("src/main/resources/application.conf"))

flywayUrl := conf.getString("db.default.url")

flywayUser := conf.getString("db.default.user")

// generate models

scalikejdbcSettings
