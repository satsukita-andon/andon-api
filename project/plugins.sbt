resolvers += "Flyway" at "https://flywaydb.org/repo"

libraryDependencies += "org.postgresql" % "postgresql" % "9.4.1208"

addSbtPlugin("org.flywaydb" % "flyway-sbt" % "4.0")

addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "2.3.5")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.6")
