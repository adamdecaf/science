import sbt._
import Keys._
import java.io.File

object ExperimentSettings {
  def project(name: String): Project = project(name, file(s"./$name"))

  def project(name: String, path: File): Project =
    Project(s"scientist-$name", path).settings(ExperimentSettings.settings: _*)

  val test: Seq[Setting[_]] =
    Seq(
      libraryDependencies ++= Seq(
        "org.specs2" %% "specs2" % "2.4.15" % "test"
      )
    )

  val settings: Seq[Setting[_]] =
    Seq(
      organization := "org.adamdecaf",
      version in ThisBuild := "1-SNAPSHOT",
      scalaVersion := "2.10.4",
      crossScalaVersions := Seq("2.10.4", "2.11.5"),
      scalacOptions ++= Seq(
        "-deprecation", "-feature", "-language:postfixOps",
        "-language:implicitConversions", "-language:higherKinds", "-language:existentials", "-language:postfixOps",
        "-Ywarn-dead-code", "-Ywarn-numeric-widen", "-Ywarn-inaccessible", "-unchecked"
      ),
      scalacOptions in Test ++= Seq(
        "-language:reflectiveCalls"
      )
    ) ++ test
}
