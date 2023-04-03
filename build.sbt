name := """fragmenty-play"""
organization := "cloud.fragmenty"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.10"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "cloud.fragmenty.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "cloud.fragmenty.binders._"

libraryDependencies ++= Seq(
  "org.mongodb.scala" %% "mongo-scala-driver" % "4.9.0",
  "com.typesafe.play" %% "play-json" % "2.9.4"
)
