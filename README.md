turbo-jug-robot
===============

A Spray.io and Angular.js application for the San Antonio Java Users Group to do cool things with stuff. And maybe be useful.

See the issues for current targeted feature sets :D

Feel free to make pull requests, or add features to issues.

[SATJUG Presentation](http://prezi.com/9s_v0cz8-4tk/introduction-to-sprayio-web-services/)

## Integration tests

Start the application using by entering SBT, and running `re-start`.

Then you can execute `cucumber` and the cucumber tests will execute. WINNING!

## Auto-reloading Integration test Application
JRebel is some pretty hot stuff. What's also hot stuff is the [sbt-revolver](https://github.com/spray/sbt-revolver) plugin that can use
JRebel to automagically update your running application. Fantastic.

It requires a license from JRebel, but that's [free to get for scala stuff](https://github.com/spray/sbt-revolver#jrebel).

JRebel has changed to a more evil licensing scheme now, sadly. sbt-revolver no longer officially supports JRebel, as the devs have
stopped using it. It still works for me right now, but I'm sad to say that I am far less pleased with JRebel than I used to be.
Regardless, sbt-revolver restarts the app quickly. We're not doing J2EE nor an App Container at all. It's fast :D

I've integrated that stuff into the app, so you can simply run `re-start` See the [directions here](https://github.com/spray/sbt-revolver#hot-reloading)


## Tech Notes
SBT Plugins:
 * [IDEA project generation](https://github.com/mpeltonen/sbt-idea)
 * [sbt-revolver](https://github.com/spray/sbt-revolver)
 * [sbt-cucumber](https://github.com/skipoleschris/xsbt-cucumber-plugin)
 * [sbt-dependency-graph](https://github.com/jrudolph/sbt-dependency-graph)

Libraries:
 * [Spray.io](http://spray.io) Specifically, spray-can, spray-routing, and the testkit.
 * [spray-json](https://github.com/spray/spray-json)
 * [Slick](http://slick.typesafe.com/). This is a particularly awesome database access library. **NOT** an ORM.

Testing stuff:
 * [Cucumber-jvm](https://github.com/cucumber/cucumber-jvm)
 * [ScalaTest](http://www.scalatest.org/) Specifically the FunSpec style. Love it!
