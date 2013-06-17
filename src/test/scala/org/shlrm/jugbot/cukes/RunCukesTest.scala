package org.shlrm.jugbot.cukes

import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

//This doesn't get run by sbt:test, but I'm okay with that for now

@RunWith(classOf[Cucumber])
@Cucumber.Options(tags = Array("@wip"))
class RunCukesTest