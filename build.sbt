
name := "turbo-jug-robot"

version := "1.0"

scalaVersion := "2.10.0"

//Using spray.io versions 1.1-M7
libraryDependencies ++= Seq (
    "net.databinder.dispatch" %% "dispatch-core" % "0.10.1" % "test",
    "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test",
    "io.spray" % "spray-testkit" % "1.1-M7" % "test",
    "io.spray" % "spray-can" % "1.1-M7",
    "io.spray" % "spray-routing" % "1.1-M7",
    "io.spray" % "spray-http" % "1.1-M7",
    "com.typesafe.akka" %% "akka-actor" % "2.2-M3",
    "com.typesafe.akka" %% "akka-slf4j" % "2.2-M3",
    "ch.qos.logback" % "logback-classic" % "1.0.12",
    //Lets add the cucumber-jvm stuff and make cucumbers happen
    "info.cukes" % "cucumber-junit" % "1.1.3",
    "info.cukes" % "cucumber-scala" % "1.1.3"
    )

resolvers ++= Seq( "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
    "spray repo" at "http://repo.spray.io",
    "typesafe repo" at "http://repo.typesafe.com/typesafe/releases/",
    "releases" at "http://oss.sonatype.org/content/repositories/releases")

net.virtualvoid.sbt.graph.Plugin.graphSettings

//Adds stuff for the less-sbt compiler
seq(lessSettings:_*)

//Adds stuff for the sbt compilation of javascript and coffeescript
seq(jsSettings : _*)

//Make it compile our javascript on compile
(resourceGenerators in Compile) <+= (JsKeys.js in Compile)

//Cannot rename variables and have dependency injection still work with Angular.js
(JsKeys.variableRenamingPolicy in (Compile)) := VariableRenamingPolicy.OFF

//Have the javascript task run automatically
(compile in Compile) <<= compile in Compile dependsOn (JsKeys.js in Compile)

//Add the cucumber stuff!
seq(cucumberSettings : _*)

cucumberStepsBasePackage := "org.shlrm.jugbot.cukes"