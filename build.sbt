name := """scala-consul"""

version := "1.1.2"

scalaVersion := "2.11.7"

crossScalaVersions := Seq(scalaVersion.value)

scalacOptions ++= Seq(
  "-encoding", "UTF-8", "-deprecation", "-feature"
  ,"-Xfuture" //, "-Xverify", "-Xcheck-null"
  ,"-Ybackend:GenBCode"
  ,"-Ydelambdafy:method"
)

libraryDependencies ++= Seq(
  "org.scalatest"     %% "scalatest" % "2.2.5" % Test,
  "com.typesafe.play" %% "play-json" % "2.4.3",
  "com.typesafe.play" %% "play-ws"   % "2.4.3"
)

organization := "com.x2sy"
organizationName := "Codacy"
organizationHomepage := Some(new URL("https://www.codacy.com"))

description := "Consul Scala Client"

publishMavenStyle := true
publishTo := {
  val nexus = "http://jci.x2sy.com/nexus/content/repositories/"
  if (isSnapshot.value)
    Some("x2sy Snapshots" at nexus + "snapshots/")
  else
    Some("x2sy Releases" at nexus + "releases/")
}