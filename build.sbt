name := "test-task-c"
version := "0.1-SNAPSHOT"
scalaVersion := "2.12.4"

mainClass in assembly := Some("org.zella.titler.server.Server")

test in assembly := {}

assemblyMergeStrategy in assembly := {
//  case PathList(ps@_*) if ps.last endsWith
//    "io.netty.versions.properties" => MergeStrategy.first
      case "io.netty.versions.properties" => MergeStrategy.last
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case x => MergeStrategy.last
}

libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.13.2"

libraryDependencies += "org.asynchttpclient" % "async-http-client" % "2.1.0-RC1"

libraryDependencies += "org.jsoup" % "jsoup" % "1.11.2"

libraryDependencies += "io.vertx" % "vertx-core" % "3.5.0"

libraryDependencies += "io.vertx" % "vertx-web" % "3.5.0"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.7"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

libraryDependencies += "org.mockito" % "mockito-all" % "1.10.19" % "test"


