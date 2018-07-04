name := "WebServer"

version := "1.0"

scalaVersion := "2.12.6"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

// https://akka.io/docs/
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.13",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.13" % Test
)
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.13",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.13" % Test
)
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.3",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.3" % Test
)

scalacOptions += "-deprecation"

// fork in run := true
// connectInput in run := true
