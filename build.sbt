import com.typesafe.startscript.StartScriptPlugin

seq(StartScriptPlugin.startScriptForClassesSettings: _*)

name := "turbo-jug-robot"

version := "1.0"

scalaVersion := "2.10.0"

//Using spray.io versions 1.1-M8
libraryDependencies ++= Dependencies.jugbot

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
