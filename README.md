turbo-jug-robot
===============

A Spray.io and Angular.js application for the San Antonio Java Users Group to do cool things with stuff. And maybe be useful.

See the issues for current targeted feature sets :D

Feel free to make pull requests, or add features to issues.

## Integration tests

I've got cucumber features files in development, but I'm sure I'm not doing it perfectly right.
Right now, you have to start the integration environment by hand:

```
sbt 'run-main org.shlrm.jugbot.IntegrationMain'
```

Doing this will start up a database in memory, start up a TCP H2 Server, so that other things can connect to it,
and then do the normal things, like flyway migrations, and then the actual spray-can server its self.

Then, you can run the Cucumber Tests from within your browser using Junit. once the
[cucumber sbt plugin](https://github.com/skipoleschris/xsbt-cucumber-plugin) gets [fixed](https://github.com/skipoleschris/xsbt-cucumber-plugin/issues/27)
I'll update this stuff with that functionality.

## Auto-reloading Integration test Application
JRebel is some pretty hot stuff. What's also hot stuff is the [sbt-revolver](https://github.com/spray/sbt-revolver) plugin that can use
JRebel to automagically update your running application. Fantastic.

It requires a license from JRebel, but that's [free to get for scala stuff](https://github.com/spray/sbt-revolver#jrebel).

I've integrated that stuff into the app, so you can simply run `re-start` See the [directions here](https://github.com/spray/sbt-revolver#hot-reloading)


## Tech Notes
SBT Plugins:
 * [Javascript/coffeescript compilation](https://github.com/untyped/sbt-plugins/tree/master/sbt-js)
 * [Less/Css Compilation](https://github.com/softprops/less-sbt)
 * [IDEA project generation](https://github.com/mpeltonen/sbt-idea)
 * [sbt-revolver](https://github.com/spray/sbt-revolver)

Libraries:
 * [Spray.io](http://spray.io) Specifically, spray-can, spray-routing, and the testkit.
 * [spray-json](https://github.com/spray/spray-json)
 * [Slick](http://slick.typesafe.com/). This is a particularly awesome database access library. **NOT** an ORM.

Testing stuff:
 * [Cucumber-jvm](https://github.com/cucumber/cucumber-jvm)
 * [ScalaTest](http://www.scalatest.org/) Specifically the FunSpec style. Love it!
