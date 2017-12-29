name := "akka-demo"

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.twitter" %% "finagle-http" % "17.10.0",
  "com.typesafe.akka" %% "akka-actor" % "2.5.6",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.6" % Test,
  "com.typesafe.akka" %% "akka-stream" % "2.5.6",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.6" % Test
)
