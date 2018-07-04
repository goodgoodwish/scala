name := "BasicScalaTest"

version := "1.0"

scalaVersion := "2.12.5"

// resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

// libraryDependencies += "org.scalatest" %% "scalatest" % "latest.integration" % "test"
// libraryDependencies += "org.scalatest" %% "scalatest" % "latest.milestone" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
// libraryDependencies += "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test"

scalacOptions += "-deprecation"

