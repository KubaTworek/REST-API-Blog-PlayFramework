name := """rest-api-blog-play-sql"""
organization := "pl.jakubtworek"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.9"
PlayKeys.externalizeResourcesExcludes += baseDirectory.value / "conf" / "META-INF" / "persistence.xml"

libraryDependencies += guice
libraryDependencies ++= Seq(
  javaJdbc
)
libraryDependencies ++= Seq(
  javaJpa,
  "org.hibernate" % "hibernate-core" % "5.4.9.Final" // replace by your jpa implementation
)
libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "8.0.30",
  "com.palominolabs.http" % "url-builder" % "1.1.4",
  "io.dropwizard.metrics" % "metrics-core" % "4.1.1",
  "net.jodah" % "failsafe" % "2.3.1"
)
