package se.yankov.zioapp

import zio.*
import zio.config.*
import zio.http.*
import zio.http.Path.*
import zio.http.netty.NettyConfig
import zio.logging.backend.SLF4J

import api.*
import domain.item.ItemService
import implementation.postgres.ItemRepositoryImplementation

object ZioApp extends ZIOAppDefault:

  // override val bootstrap: ULayer[Unit] = Runtime.removeDefaultLoggers >>> SLF4J.slf4j

  // private val dataSourceLayer = Quill.DataSource.fromPrefix("db")

  // private val postgresLayer = Quill.Postgres.fromNamingStrategy(Literal)

  // private val repoLayer = ItemRepositoryImplementation.layer

  // private val healthCheckServiceLayer = HealthCheckServiceLive.layer

  // private val serverLayer =
  //   ZLayer
  //     .service[ApiConfig]
  //     .flatMap { cfg =>
  //       Server.defaultWith(_.binding(cfg.get.host, cfg.get.port))
  //     }
  //     .orDie

  // val routes = HttpRoutes.app ++ HealthCheckRoutes.app

  // private val program = Server.serve(routes)

  private def publicApiProgram(port: Int): RIO[PublicApiHandler, Nothing] =
    (ZIO.serviceWithZIO[PublicApiHandler](handlers => Server.install(PublicApi.api.withDefaultErrorResponse)) *>
      ZIO.logDebug(s"Public API server started on port $port") *>
      ZIO.never)
      .provideSomeLayer(
        ZLayer.succeed(Server.Config.default.port(port)) ++
          ZLayer.succeed(NettyConfig.default.leakDetection(NettyConfig.LeakDetectionLevel.PARANOID)) >>>
          Server.customized
      )

  override val run: UIO[ExitCode] =
    publicApiProgram(1337)
      .provide(
        (Runtime.removeDefaultLoggers >>> SLF4J.slf4j) ++
          AppConfig.layer >>>
          implementation.layer >>> domain.layer >>> PublicApiHandler.layer
      )
      .foldCauseZIO(
        error => ZIO.logError(s"Program failed: ${error.squash.getMessage}") *> ZIO.succeed(ExitCode.failure),
        _ => ZIO.succeed(ExitCode.success),
      )
