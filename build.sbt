import sbt._
import Keys._

lazy val root = Project("science-root", file("."))
  .aggregate(core, fs)
  .settings(publish := {}, publishLocal := {})

lazy val core = Project("science-core", file("./core"))
  .settings(name := "science-core")
  .settings(baseSettings: _*)
  .settings(libraryDependencies ++= Seq(
              "org.slf4j" % "slf4j-api" % "1.7.12",
              "org.slf4j" % "log4j-over-slf4j" % "1.7.12"
            ))

lazy val fs = Project("science-fs", file("./fs"))
  .dependsOn(core)
  .settings(name := "science-fs")
  .settings(baseSettings: _*)

val baseSettings: Seq[Setting[_]] =
  Seq(
    organization := "org.decaf",
    scalaVersion := "2.11.6",
    crossScalaVersions := Seq("2.10.5", "2.11.6"),
    version in ThisBuild := "1-SNAPSHOT",

    scalacOptions ++= Seq(
      "-deprecation", "-feature", "-Ywarn-dead-code", "-Ywarn-numeric-widen", "-Ywarn-inaccessible", "-unchecked"
    ),

    // specs2
    resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
    scalacOptions in Test ++= Seq("-Yrangepos"),
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-core" % "3.6.1" % "test"
    )
  )
