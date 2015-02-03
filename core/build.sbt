import sbt._

val metricsVersion = "3.1.0"

libraryDependencies ++= Seq(
  "io.dropwizard.metrics" % "metrics-core" % metricsVersion,
  "nl.grons" %% "metrics-scala" % "3.3.0_a2.3"
)
