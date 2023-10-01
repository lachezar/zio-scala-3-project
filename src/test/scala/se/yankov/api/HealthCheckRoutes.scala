package se.yankov.api

import se.yankov.api.healthcheck.HealthCheckServiceTest
import zio.http.*
import zio.http.Path.*
import zio.test.*
import zio.test.Assertion.*

object HealthCheckRoutesSpec extends ZIOSpecDefault:

  val specs = suite("http")(
    suite("health check")(
      test("ok status") {
        val actual =
          HealthCheckRoutes.app.runZIO(Request.get(URL(Root / "healthcheck")))
        assertZIO(actual)(equalTo(Response(Status.Ok, Headers.empty, Body.empty)))
      }
    )
  )

  override def spec = specs.provide(
    HealthCheckServiceTest.layer
  )
