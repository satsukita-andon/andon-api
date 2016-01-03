resolvers += "Flyway" at "http://flywaydb.org/repo"

libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1206-jdbc42"

addSbtPlugin("org.flywaydb" % "flyway-sbt" % "3.1")

addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "2.3.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.6")
