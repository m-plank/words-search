val Http4sVersion = "0.20.11"
val CirceVersion = "0.11.1"
val Specs2Version = "4.1.0"
val LogbackVersion = "1.2.3"
val JodaTimeVersion = "2.10.3"
val mongoDriverVersion = "2.7.0"
val mtlVersion = "0.7.0"
val catsScalaTestVersion = "3.0.0"
val scalaTestVersion = "3.0.8"
val reactiveMongoDriver = "0.18.8"

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    organization := "com.example",
    name := "words-search",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.9",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.typelevel" %% "cats-mtl-core" % mtlVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "joda-time" % "joda-time" % JodaTimeVersion,
      "org.specs2" %% "specs2-core" % Specs2Version % "it,test",
      "com.ironcorelabs" %% "cats-scalatest" % catsScalaTestVersion % "test",
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "org.mongodb.scala" %% "mongo-scala-driver" % mongoDriverVersion,
      "org.reactivemongo" %% "reactivemongo" % reactiveMongoDriver
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.0"),
    mainClass in Compile := Some("words.search.wiki.Main")
  )
  .enablePlugins(JavaAppPackaging, DockerPlugin)

val funTestFilter: String => Boolean = { name =>
  (name.endsWith("ItTest")) || (name.endsWith("IntegrationTest"))
}

addCommandAlias("co", ";clean;coverage;test;coverageReport")

testOptions in IntegrationTest += Tests.Filter(funTestFilter)

resolvers += "Sonatype OSS Snapshots".at("https://oss.sonatype.org/content/repositories/snapshots")

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Ypartial-unification",
  "-Xfatal-warnings"
)
