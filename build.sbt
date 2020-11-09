import scoverage.ScoverageKeys

name := "gfc-vavr"

organization := "org.gfccollective"

scalaVersion := "2.13.3"

crossScalaVersions := Seq(scalaVersion.value, "2.12.12")

scalacOptions += "-target:jvm-1.8"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

libraryDependencies ++= Seq(
  "io.vavr" % "vavr" % "0.10.3",
  "org.scalatest" %% "scalatest" % "3.2.3" % Test
)

ScoverageKeys.coverageMinimum := 100.0

ScoverageKeys.coverageFailOnMinimum := true

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

licenses := Seq("Apache-style" -> url("https://raw.githubusercontent.com/gfc-collective/gfc-vavr/master/LICENSE"))

homepage := Some(url("https://github.com/gfc-collective/gfc-vavr"))

pomExtra := (
  <scm>
    <url>https://github.com/gfc-collective/gfc-vavr.git</url>
    <connection>scm:git:git@github.com:gfc-collective/gfc-vavr.git</connection>
  </scm>
  <developers>
    <developer>
      <id>sullis</id>
      <name>Sean Sullivan</name>
      <url>https://github.com/sullis</url>
    </developer>
  </developers>
)
