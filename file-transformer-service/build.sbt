name := """file-transformer-service"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)

libraryDependencies ++= Seq(
  javaJpa,
  "org.hibernate" % "hibernate-entitymanager" % "5.2.4.Final",
  "dom4j" % "dom4j" % "1.6.1" intransitive(),
  "org.apache.activemq" % "activemq-camel" % "5.14.1",
  "org.mockito" % "mockito-core" % "2.2.25" % "test"

) 

