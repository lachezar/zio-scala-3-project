val zioVersion            = "2.0.22"
val zioHttpVersion        = "3.0.0-RC6"
val zioKafkaVersion       = "2.7.4"
val zioJsonVersion        = "0.6.2"
val zioPreludeVersion     = "1.0.0-RC23"
val zioConfigVersion      = "4.0.1"
val zioLoggingVersion     = "2.2.2"
val logbackClassicVersion = "1.5.4"
val quillVersion          = "4.8.3"
val postgresqlVersion     = "42.7.3"
val flywayVersion         = "10.11.0"
val chimneyVersion        = "0.8.5"
val testContainersVersion = "0.41.3"
val zioMockVersion        = "1.0.0-RC12"

lazy val quillNamingStrategy =
  (project in file("quill"))
    .settings(
      name                                  := "zioapp-quill",
      libraryDependencies ++= "io.getquill" %% "quill-jdbc-zio" % quillVersion :: Nil,
    )

lazy val root = (project in file("."))
  .settings(
    inThisBuild(
      List(
        organization := "se.yankov",
        scalaVersion := "3.3.3",
      )
    ),
    name                    := "zio-scala-3-project",
    libraryDependencies ++= Seq(
      "io.getquill"   %% "quill-jdbc-zio"             % quillVersion excludeAll (
        ExclusionRule(organization = "org.scala-lang.modules")
      ),
      "org.postgresql" % "postgresql"                 % postgresqlVersion,
      "org.flywaydb"   % "flyway-core"                % flywayVersion,
      "org.flywaydb"   % "flyway-database-postgresql" % flywayVersion,
      "dev.zio"       %% "zio"                        % zioVersion,
      "dev.zio"       %% "zio-http"                   % zioHttpVersion,
      "dev.zio"       %% "zio-kafka"                  % zioKafkaVersion,
      "dev.zio"       %% "zio-config"                 % zioConfigVersion,
      "dev.zio"       %% "zio-config-typesafe"        % zioConfigVersion,
      "dev.zio"       %% "zio-config-magnolia"        % zioConfigVersion,
      "dev.zio"       %% "zio-json"                   % zioJsonVersion,
      "io.scalaland"  %% "chimney"                    % chimneyVersion,
      "dev.zio"       %% "zio-prelude"                % zioPreludeVersion,

      // logging
      "dev.zio"       %% "zio-logging"       % zioLoggingVersion,
      "dev.zio"       %% "zio-logging-slf4j" % zioLoggingVersion,
      "ch.qos.logback" % "logback-classic"   % logbackClassicVersion,

      // test
      "dev.zio"      %% "zio-test"                        % zioVersion            % Test,
      "dev.zio"      %% "zio-test-sbt"                    % zioVersion            % Test,
      "dev.zio"      %% "zio-test-junit"                  % zioVersion            % Test,
      "dev.zio"      %% "zio-mock"                        % zioMockVersion        % Test,
      "com.dimafeng" %% "testcontainers-scala-postgresql" % testContainersVersion % Test,
      "dev.zio"      %% "zio-test-magnolia"               % zioVersion            % Test,
    ),
    testFrameworks          := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
    // try using the `tpolecatScalacOptions` configuration key for any additional compiler flags
    Compile / doc / sources := Seq.empty,
  )
  .dependsOn(quillNamingStrategy)
  .enablePlugins(JavaAppPackaging, UniversalPlugin)

addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")

wartremoverErrors ++= Warts.unsafe diff Seq(Wart.Any, Wart.DefaultArguments)
