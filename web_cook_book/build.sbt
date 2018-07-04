name := "web_cook_book"

version := "1.0"

scalaVersion := "2.12.6"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

// libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.11"
// libraryDependencies += "com.typesafe.play" %% "play" % "2.6.12"
// libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.9"
// libraryDependencies += "net.liftweb" %% "lift-json" % "3.3.0-M1"

libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.5"
libraryDependencies += "com.lihaoyi" %% "ujson" % "0.6.6"
libraryDependencies += "com.typesafe.akka" %% "akka-http"   % "10.1.3" 
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.12"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.2.3",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3"
)
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.2"

scalacOptions += "-deprecation"

