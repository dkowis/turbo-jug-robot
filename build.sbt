import com.typesafe.startscript.StartScriptPlugin

seq(StartScriptPlugin.startScriptForClassesSettings: _*)

name := "turbo-jug-robot"

version := "1.0"

scalaVersion := "2.10.0"

//Using spray.io versions 1.1-M8
libraryDependencies ++= Seq (
    "net.databinder.dispatch" %% "dispatch-core" % "0.10.1" % "test",
    "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test",
    "io.spray" % "spray-testkit" % "1.1-M8" % "test",
    "com.googlecode.flyway" % "flyway-core" % "2.1.1",
    "com.typesafe" % "config" % "1.0.2",
    //Adding JSON support using spray-json
    "io.spray" %%  "spray-json" % "1.2.5",
    "io.spray" % "spray-can" % "1.1-M8",
    "io.spray" % "spray-routing" % "1.1-M8",
    "com.typesafe.akka" %% "akka-actor" % "2.1.4",
    "com.typesafe.akka" %% "akka-slf4j" % "2.1.4",
    "ch.qos.logback" % "logback-classic" % "1.0.12",
    //Lets add the cucumber-jvm stuff and make cucumbers happen
    "info.cukes" % "cucumber-junit" % "1.1.3",
    "info.cukes" % "cucumber-scala" % "1.1.3",
    //Adding slick, and database stuff
    "com.typesafe.slick" %% "slick" % "1.0.1-RC1",
    "com.h2database" % "h2" % "1.3.166",
    "postgresql" % "postgresql" % "9.1-901.jdbc4"
    )

resolvers ++= Seq( "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
    "spray repo" at "http://repo.spray.io",
    "typesafe repo" at "http://repo.typesafe.com/typesafe/releases/",
    "releases" at "http://oss.sonatype.org/content/repositories/releases")

net.virtualvoid.sbt.graph.Plugin.graphSettings

//Add the cucumber stuff!
seq(cucumberSettings : _*)

cucumberStepsBasePackage := "org.shlrm.jugbot.cukes"

//Jrebel hot reloading!
seq(Revolver.settings: _*)

//Define the class we want to reload all the time
mainClass in Revolver.reStart := Some("org.shlrm.jugbot.IntegrationMain")

//Set the main class for normal execution
mainClass in Compile := Some("org.shlrm.jugbot.Main")
