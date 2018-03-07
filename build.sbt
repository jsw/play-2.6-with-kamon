name := """play-2.6-with-kamon"""
organization := "jsw"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, JavaAgent)

scalaVersion := "2.12.4"

libraryDependencies += ws
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
libraryDependencies += "com.markatta" %% "futiles" % "2.0.0"

libraryDependencies += "io.kamon" %% "kamon-core" % "1.0.0"
libraryDependencies += "io.kamon" %% "kamon-play-2.6" % "1.0.2"
libraryDependencies += "io.kamon" %% "kamon-logback" % "1.0.0"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "jsw.binders._"

javaAgents += "org.aspectj" % "aspectjweaver" % "1.8.13" // (2)
javaOptions in Universal += "-Dorg.aspectj.tracing.factory=default" // (3)
