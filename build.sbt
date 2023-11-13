val zioVersion            = "2.0.18"
val zioHttpVersion        = "3.0.0-RC3"
val zioKafkaVersion       = "2.6.0"
val zioJsonVersion        = "0.6.2"
val zioPreludeVersion     = "1.0.0-RC21"
val zioConfigVersion      = "3.0.7"
val zioLoggingVersion     = "2.1.14"
val logbackClassicVersion = "1.4.11"
val quillVersion          = "4.8.0"
val postgresqlVersion     = "42.6.0"
val flywayVersion         = "10.0.0"
val chimneyVersion        = "0.8.1"
val testContainersVersion = "0.41.0"
val zioMockVersion        = "1.0.0-RC11"

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
        scalaVersion := "3.3.1",
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
