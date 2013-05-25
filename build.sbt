
name := "turbo-jug-robot"

version := "1.0"

scalaVersion := "2.10.0"

//Using spray.io versions 1.1-M7
libraryDependencies ++= Seq (
    "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test",
    "io.spray" % "spray-servlet" % "1.1-M7",
    "io.spray" % "spray-http" % "1.1-M7",
    "com.typesafe.akka" %% "akka-actor" % "2.2-M3",
    "com.typesafe.akka" %% "akka-slf4j" % "2.2-M3",
    //Servlet API?
    //"org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" artifacts Artifact("javax.servlet", "jar", "jar"),
   "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided",
    "ch.qos.logback" % "logback-classic" % "1.0.12",
    //Doesn't work with jetty 9.0 yet, not sure what the deal is
    "org.eclipse.jetty" % "jetty-webapp" % "8.0.1.v20110908" % "container"
    )

resolvers ++= Seq( "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
    "spray repo" at "http://repo.spray.io",
    "typesafe repo" at "http://repo.typesafe.com/typesafe/releases/",
    "releases" at "http://oss.sonatype.org/content/repositories/releases")

net.virtualvoid.sbt.graph.Plugin.graphSettings

//Adding stuff for the web plugin
seq(webSettings :_*)

//Adds stuff for the sbt compilation of javascript and coffeescript
seq(jsSettings : _*)

//Add the output files from this into the webappResources
(webappResources in Compile) <+= (resourceManaged in Compile)

//Settings for the Javascript compilation stuff:
(resourceManaged in (Compile, JsKeys.js)) <<= (sourceDirectory in Compile)(_ / "webapp")

//Cannot rename variables and have dependency injection still work with Angular.js
(JsKeys.variableRenamingPolicy in (Compile)) := VariableRenamingPolicy.OFF
