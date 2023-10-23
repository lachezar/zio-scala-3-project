package se.yankov.zioapp
package api

import zio.*
import zio.http.*
import zio.json.*
import zio.test.*
import zio.test.TestAspect.*

import api.item.ItemResult
import domain.*
import domain.common.Money
import domain.events.*
import domain.item.*
import implementation.auth.AuthService
import implementation.json.ItemCodecs.given
import implementation.json.given

import java.util.UUID

object PrivateApiSpec extends ZIOSpecDefault:

  private val uuid0           = new UUID(0, 0)
  private val createItemInput = CreateItemInput[ValidationStatus.NonValidated.type]("lego", Money(35), ProductType.Toys)
  private val item            = Item(ItemId(uuid0), "lego", Money(35), ProductType.Toys)

  val specs = suite("private http")(
    suite("items api")(
      test("requires authorization") {
        val actual =
          TestRandom.feedUUIDs(uuid0) *>
            PrivateApi
              .api
              .runZIO(
                Request
                  .post(URL(Root / "items"), Body.fromString(createItemInput.toJson))
                  .addHeader(Header.ContentType(MediaType.application.json))
              )
        assertZIO(actual.map(_.status))(Assertion.equalTo(Status.Unauthorized))
      },
      test("create item") {
        val actual =
          TestRandom.feedUUIDs(uuid0) *>
            PrivateApi
              .api
              .runZIO(
                Request
                  .post(URL(Root / "items"), Body.fromString(createItemInput.toJson))
                  .addHeader(Header.Authorization.Bearer("token"))
                  .addHeader(Header.ContentType(MediaType.application.json))
              )
        assertZIO(actual)(Assertion.equalTo(Response.json(ItemResult.fromDomain(item).toJson)))
      },
      test("view item") {
        val actual =
          PrivateApi
            .api
            .runZIO(
              Request
                .get(URL(Root / "items" / uuid0.toString))
                .addHeader(Header.Authorization.Bearer("token"))
                .addHeader(Header.ContentType(MediaType.application.json))
            )
        assertZIO(actual)(Assertion.equalTo(Response.json(ItemResult.fromDomain(item).toJson)))
      },
      test("delete item") {
        val actual =
          PrivateApi
            .api
            .runZIO(
              Request
                .delete(URL(Root / "items" / uuid0.toString))
                .addHeader(Header.Authorization.Bearer("token"))
                .addHeader(Header.ContentType(MediaType.application.json))
            )
        assertZIO(actual)(Assertion.equalTo(Response.json(().toJson)))
      },
    )
  ) @@ sequential

  override def spec = specs.provideShared(
    PrivateApiHandler.layer,
    AuthService.layer,
    ItemService.layer,
    InMemoryItemRepository.layer,
    EventPublisherMock.optionalMockLayer,
  )
