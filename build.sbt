name := "dc-env"

version := "1.0"

scalaVersion := "2.11.12"

lazy val env = (project in file("."))

lazy val proxy = (project in file("dc-proxy")).dependsOn(env)

lazy val crawler = (project in file("dc-crawler")).dependsOn(env)

lazy val diplomat = (project in file("dc-diplomat")).dependsOn(env,proxy,crawler)

libraryDependencies ++= Dependencies.libDependencies
