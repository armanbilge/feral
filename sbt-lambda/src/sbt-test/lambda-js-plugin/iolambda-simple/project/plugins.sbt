resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
addSbtPlugin("org.typelevel" % "sbt-feral-lambda" % sys.props("plugin.version"))
