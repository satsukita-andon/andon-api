name := "andon-api"

organization := "com.satsukita-andon"

version := "0.0.0"

scalaVersion := "2.11.8"

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
  val twitterServer = "1.18.0"
  val finch = "0.10.0"
  val circe = "0.3.0"
  val scalikejdbc = "2.3.5"
  val postgres = "9.4.1208"
  val hikaricp = "2.4.4"
  val scalatest = "2.2.6"
  val jwt = "0.6.0"
  val joda = "2.9.2"
  val shapeless = "2.2.5"
  val cats = "0.4.1"
  val config = "1.3.0"
  val commonsValidator = "1.5.0"
  val commonsIo = "2.4"
  val scrimage = "2.1.5"
  Seq(
    "com.twitter" %% "twitter-server" % twitterServer,
    "com.github.finagle" %% "finch-core" % finch,
    "com.github.finagle" %% "finch-circe" % finch,
    "com.github.finagle" %% "finch-test" % finch % "test",
    "io.circe" %% "circe-core" % circe,
    "io.circe" %% "circe-generic" % circe,
    "io.circe" %% "circe-parser" % circe,
    "com.pauldijou" %% "jwt-core" % jwt,
    "org.scalikejdbc" %% "scalikejdbc" % scalikejdbc,
    "org.scalikejdbc" %% "scalikejdbc-test" % scalikejdbc % "test",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "org.postgresql" % "postgresql" % postgres,
    "com.zaxxer" % "HikariCP" % hikaricp,
    "org.scalatest" %% "scalatest" % scalatest % "test",
    "joda-time" % "joda-time" % joda,
    "com.chuusai" %% "shapeless" % shapeless,
    "org.typelevel" %% "cats" % cats,
    "com.typesafe" % "config" % config,
    "com.github.jeremyh" % "jBCrypt" % "jbcrypt-0.4",
    "commons-validator" % "commons-validator" % commonsValidator,
    "commons-io" % "commons-io" % commonsIo,
    "com.sksamuel.scrimage" %% "scrimage-core" % scrimage,
    "com.sksamuel.scrimage" %% "scrimage-io-extra" % scrimage
  )
}

// db migration using flyway

flywayUrl := "jdbc:postgresql://localhost/andon" // or andon_test

flywayUser := "amutake"

// generate models

scalikejdbcSettings

parallelExecution in Test := false

enablePlugins(JavaAppPackaging)
