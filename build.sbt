name := "spray-json"

version := "1.0.0-SNAPSHOT"

organization := "cc.spray.json"

scalaVersion := "2.9.0-1"

libraryDependencies ++= Seq(
  "org.parboiled" % "parboiled-core" % "1.0.0" % "compile",
  "org.parboiled" % "parboiled-scala" % "1.0.0" % "compile",
  "org.scala-tools.testing" %% "specs" % "1.6.8" % "test"
)

scalacOptions ++= Seq("-deprecation", "-encoding", "utf8")

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishTo <<= version { v => Some(if (v endsWith "-SNAPSHOT") ScalaToolsSnapshots else ScalaToolsReleases) }

pomExtra := (
  <name>spray JSON</name>
  <url>http://spray.cc/</url>
  <inceptionYear>2011</inceptionYear>
  <description>A Scala library for easy and idiomatic JSON (de)serialization</description>
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <developers>
    <developer>
      <id>sirthias</id>
      <name>Mathias Doenitz</name>
      <timezone>+1</timezone>
      <email>mathias [at] spray.cc</email>
    </developer>
  </developers>
  <scm>
    <url>http://github.com/spray/spray-json/</url>
  </scm>
)