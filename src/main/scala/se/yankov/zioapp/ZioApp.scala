package se.yankov.zioapp

import zio.*
import zio.http.*
import zio.http.netty.NettyConfig
import zio.logging.backend.SLF4J

import api.*
import implementation.postgres.Migration

object ZioApp extends ZIOAppDefault:

  private def publicApiProgram(port: Int): RIO[PublicApiHandler, Nothing] =
    (ZIO.serviceWithZIO[PublicApiHandler](handlers => Server.install(PublicApi.api.withDefaultErrorResponse)) *>
      ZIO.logDebug(s"Public API server started on port $port") *>
      ZIO.never)
      .provideSomeLayer(
        ZLayer.succeed(Server.Config.default.port(port)) ++
          ZLayer.succeed(NettyConfig.default.leakDetection(NettyConfig.LeakDetectionLevel.PARANOID)) >>>
          Server.customized
      )

  private def privateApiProgram(port: Int): RIO[PrivateApiHandler, Nothing] =
    (ZIO.serviceWithZIO[PrivateApiHandler](handlers => Server.install(PrivateApi.api.withDefaultErrorResponse)) *>
      ZIO.logDebug(s"Private API server started on port $port") *>
      ZIO.never)
      .provideSomeLayer(
        ZLayer.succeed(Server.Config.default.port(port)) ++
          ZLayer.succeed(NettyConfig.default.leakDetection(NettyConfig.LeakDetectionLevel.PARANOID)) >>>
          Server.customized
      )

  private def internalApiProgram(port: Int): RIO[InternalApiHandler, Nothing] =
    (ZIO.serviceWithZIO[InternalApiHandler](handlers => Server.install(InternalApi.api.withDefaultErrorResponse)) *>
      ZIO.logDebug(s"Internal API server started on port $port") *>
      ZIO.never)
      .provideSomeLayer(
        ZLayer.succeed(Server.Config.default.port(port)) ++
          ZLayer.succeed(NettyConfig.default.leakDetection(NettyConfig.LeakDetectionLevel.PARANOID)) >>>
          Server.customized
      )

  override val run: UIO[ExitCode] =
    (Migration.run *>
      ZIO.raceFirst(publicApiProgram(1337), privateApiProgram(1338) :: internalApiProgram(1339) :: Nil))
      .provide(
        (Runtime.removeDefaultLoggers >>> SLF4J.slf4j) ++
          AppConfig.layer >+>
          (implementation.layer >+> domain.layer >>> (PublicApiHandler.layer ++ PrivateApiHandler.layer ++ InternalApiHandler.layer))
      )
      .foldCauseZIO(
        error => ZIO.logError(s"Program failed: ${error.squash.getMessage}") *> ZIO.succeed(ExitCode.failure),
        _ => ZIO.succeed(ExitCode.success),
      )
