import scoverage.ScoverageKeys

name := "gfc-vavr"

organization := "org.gfccollective"

scalaVersion := "2.13.13"

crossScalaVersions := Seq(scalaVersion.value, "2.12.19", "3.3.3")

scalacOptions += "-target:jvm-1.8"

scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, _)) =>
          Seq("-source:3.0-migration", "-explain", "-explain-types")
        case _ =>
          Nil
      }
    }

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

libraryDependencies ++= Seq(
  "io.vavr" % "vavr" % "0.10.4",
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)

ScoverageKeys.coverageMinimumStmtTotal := 100.0

ScoverageKeys.coverageFailOnMinimum := true

publishMavenStyle := true

Test / publishArtifact := false

pomIncludeRepository := { _ => false }

licenses := Seq("Apache-style" -> url("https://raw.githubusercontent.com/gfc-collective/gfc-vavr/main/LICENSE"))

homepage := Some(url("https://github.com/gfc-collective/gfc-vavr"))

pomExtra := (
  <developers>
    <developer>
      <id>sullis</id>
      <name>Sean Sullivan</name>
      <url>https://github.com/sullis</url>
    </developer>
  </developers>
)
