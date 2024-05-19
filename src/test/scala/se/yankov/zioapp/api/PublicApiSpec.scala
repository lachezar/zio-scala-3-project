package se.yankov.zioapp
package api

import zio.*
import zio.http.*
import zio.test.*

import domain.events.*
import domain.item.*

object PublicApiSpec extends ZIOSpecDefault:

  val specs = suite("public http")(
    suite("health check")(
      test("ok status") {
        val actual =
          PublicApi.api.runZIO(Request.get(URL(Path.root / "health")))
        assertZIO(actual)(Assertion.equalTo(Response.text("ok")))
      }
    )
  )

  override def spec = specs.provide(
    PublicApiHandler.layer,
    ItemService.layer,
    InMemoryItemRepository.layer,
    EventPublisherMock.optionalMockLayer,
  )
