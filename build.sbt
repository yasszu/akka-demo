name := "akka-sample"

version := "1.0"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.twitter" %% "finagle-http" % "17.10.0",
  "com.typesafe.akka" %% "akka-actor" % "2.5.6",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.6" % Test,
  "org.apache.kafka" %% "kafka" % "0.10.2.2"
)
