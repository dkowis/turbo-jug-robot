resolvers += Resolver.url(
  "sbt-plugin-releases",
  url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/")
)(Resolver.ivyStylePatterns)

resolvers += "Templemore Repository" at "http://templemore.co.uk/repo"

addSbtPlugin("com.untyped" %% "sbt-js" % "0.5")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.4.0")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.3")

addSbtPlugin("me.lessis" % "less-sbt" % "0.1.10")

addSbtPlugin("templemore" % "sbt-cucumber-plugin" % "0.7.2")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.6.2")
