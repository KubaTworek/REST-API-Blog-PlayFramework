lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    name := """rest-api-blog-play-sql""",
    organization := "pl.jakubtworek",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.9",
    libraryDependencies ++= Seq(
      guice,
      javaJpa,
      javaJdbc,
      "mysql" % "mysql-connector-java" % "8.0.30",
      "org.hibernate" % "hibernate-core" % "5.5.6",
      "com.palominolabs.http" % "url-builder" % "1.1.4",
      "io.dropwizard.metrics" % "metrics-core" % "4.2.12",
      "net.jodah" % "failsafe" % "2.4.4"
    ),
    PlayKeys.externalizeResources := false,
    (Test / testOptions) := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v")),
    javacOptions ++= Seq(
      "-Xlint:unchecked",
      "-Xlint:deprecation",
      "-Werror"
    )
  )