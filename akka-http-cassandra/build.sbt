lazy val akkaVersion = "2.5.9"
lazy val akkaHttpVersion = "10.1.0"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.example",
      scalaVersion    := "2.12.4"
    )),
    name := "akka-http-cassandra",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.83",
      "ch.qos.logback" % "logback-classic" % "1.2.3"
    )
  )
