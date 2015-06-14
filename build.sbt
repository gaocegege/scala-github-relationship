lazy val root = (project in file(".")).
  settings(
    name := "sblog",
    version := "1.0",
    scalaVersion := "2.11.6",
    libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.11.6",
    libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.4.1",
    libraryDependencies += "org.jsoup" % "jsoup" % "1.8.2",
    libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.4-M1",
    libraryDependencies += "org.eclipse.mylyn.github" % "org.eclipse.egit.github.core" % "2.1.5"

  )