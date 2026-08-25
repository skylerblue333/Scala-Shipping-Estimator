ThisBuild / scalaVersion := "3.3.3"
ThisBuild / organization := "com.skycoin4444"
ThisBuild / version := "0.1.0"

lazy val root = (project in file("."))
  .settings(
    name := "sky-shipping-estimator",
    scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Werror"),
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    Test / fork := true
  )
