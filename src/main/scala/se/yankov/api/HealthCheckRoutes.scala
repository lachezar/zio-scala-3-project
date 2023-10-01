package se.yankov.api

import se.yankov.api.healthcheck.HealthCheckService
import zio.*
import zio.http.*
import zio.http.Path.*

object HealthCheckRoutes:

  val app: HttpApp[HealthCheckService, Nothing] = Http.collectZIO {

    case Method.HEAD -> Root / "healthcheck" =>
      ZIO.succeed {
        Response.status(Status.NoContent)
      }

    case Method.GET -> Root / "healthcheck" =>
      HealthCheckService.check.map { dbStatus =>
        if (dbStatus.status) Response.ok
        else Response.status(Status.InternalServerError)
      }

  }
