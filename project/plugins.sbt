addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.12")

// Required for running on development mode with instrumentation
resolvers += Resolver.bintrayIvyRepo("kamon-io", "sbt-plugins")
addSbtPlugin("io.kamon" % "sbt-aspectj-runner-play-2.6" % "1.1.0")
