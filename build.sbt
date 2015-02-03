import sbt._
import Keys._

import ExperimentSettings.project

lazy val root = project("root", file(".")).aggregate(core).settings(publish := {})

lazy val core = project("core")
