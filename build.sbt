name := """play-2.6-with-kamon"""
organization := "jsw"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

libraryDependencies += ws
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
libraryDependencies += "com.markatta" %% "futiles" % "2.0.0"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "jsw.binders._"
