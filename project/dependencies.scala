import sbt._
import Keys._

object Library {
  //versions
  val sprayVersion = "1.1-M8"
  val akkaVersion = "2.1.4"
  val cucumberVersion = "1.1.4"
  val logbackVersion = "1.0.13"
  val sprayJsonVersion = "1.2.5"
  val dispatchVersion = "0.10.1"
  val scalaTestVersion = "1.9.1"
  val flywayVersion = "2.1.1"
  val typesafeConfigVersion = "1.0.2"
  val postgresqlVersion = "9.1-901.jdbc4"
  val slickVersion = "1.0.1-RC1"
  val h2Version = "1.3.166"
  val junitInterfaceVersion = "0.10"

  //testing dependencies
  val dispatch = "net.databinder.dispatch" %% "dispatch-core" % dispatchVersion
  val scalaTest = "org.scalatest" % "scalatest_2.10" % scalaTestVersion
  val sprayTestkit = "io.spray" % "spray-testkit" % sprayVersion
  val cucumberJunit = "info.cukes" % "cucumber-junit" % cucumberVersion
  val cucumberScala = "info.cukes" %% "cucumber-scala" % cucumberVersion
  val junitInterface = "com.novocode" % "junit-interface" % junitInterfaceVersion

  //Primary app depends
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  val logbackClassic = "ch.qos.logback" % "logback-classic" % logbackVersion
  val sprayCan = "io.spray" % "spray-can" % sprayVersion
  val sprayRouting = "io.spray" % "spray-routing" % sprayVersion
  val sprayJson = "io.spray" %% "spray-json" % sprayJsonVersion
  val flyway = "com.googlecode.flyway" % "flyway-core" % flywayVersion
  val typesafeConfig = "com.typesafe" % "config" % typesafeConfigVersion

  val postgresql = "postgresql" % "postgresql" % postgresqlVersion
  val h2 = "com.h2database" % "h2" % h2Version
  val slick = "com.typesafe.slick" %% "slick" % slickVersion


}

object Dependencies {

  import Library._

  val jugbot = List(
    dispatch % "test",
    scalaTest % "test",
    sprayTestkit % "test",
    cucumberJunit % "test",
    cucumberScala % "test",
    akkaActor,
    akkaSlf4j,
    logbackClassic,
    sprayCan,
    sprayRouting,
    sprayJson,
    flyway,
    typesafeConfig,
    h2,
    postgresql,
    slick
  )
}