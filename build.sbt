name := "akka-sample"

version := "1.0"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.twitter" %% "twitter-server" % "19.7.0",
  "com.twitter" %% "finagle-http" % "17.10.0",
  "com.typesafe.akka" %% "akka-actor" % "2.5.23",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.23" % Test,
  "org.apache.kafka" %% "kafka" % "0.10.2.2",
  "org.slf4j" % "slf4j-simple" % "1.7.25"
)
