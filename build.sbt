name := """rest-api-blog-play-sql"""
organization := "pl.jakubtworek"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.9"

libraryDependencies += guice
