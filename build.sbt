name := "akka-sample"

version := "1.0"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.23",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.23" % Test,
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.23",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.apache.kafka" %% "kafka" % "0.10.2.2"
)
