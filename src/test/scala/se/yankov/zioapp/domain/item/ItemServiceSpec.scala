package se.yankov.zioapp
package domain
package item

import zio.*
import zio.mock.Expectation.*
import zio.test.*
import zio.test.Assertion.*

import domain.*
import domain.common.Money
import domain.events.EventPublisher
import domain.item.*
import implementation.*

import java.util.UUID

import com.github.dockerjava.api.model.Event
import io.scalaland.chimney.dsl.*

object ItemServiceSpec extends ZIOSpecDefault:

  val uuid0           = new UUID(0, 0)
  val uuid1           = new UUID(0, 1)
  val exampleItem     = Item(ItemId(uuid0), "lego", Money(99), ProductType.Toys)
  val updateItemInput =
    UpdateItemInput[ValidationStatus.Validated.type]("gameboy", Money(50), ProductType.Electronics.toString)

  val getItemMock: ULayer[ItemRepository] = ItemRepoMock.GetById(
    equalTo(ItemId(uuid0)),
    value(exampleItem),
  ) ++ ItemRepoMock.GetById(equalTo(ItemId(uuid1)), failure(RepositoryError.MissingEntity()))

  val getByNonExistingId: ULayer[ItemRepository] =
    ItemRepoMock.GetById(equalTo(ItemId(uuid1)), failure(RepositoryError.MissingEntity()))

  def updateMock(data: UpdateItemInput[ValidationStatus.Validated.type]): ULayer[ItemRepository] =
    ItemRepoMock.Update(
      hasField("id", _._1, equalTo(exampleItem.id)),
      value(
        data
          .into[Item]
          .withFieldConst(_.id, exampleItem.id)
          .withFieldComputed(_.productType, data => ProductType.valueOf(data.productType))
          .transform
      ),
    ) ++ ItemRepoMock.Update(
      hasField("id", _._1, equalTo(ItemId(uuid1))),
      failure(RepositoryError.MissingEntity()),
    )

  val eventPublisherMock: ULayer[EventPublisher] =
    events.EventPublisherMock.SendNewItemEvent(anything, value(())).optional

  def spec = suite("item service test")(
    test("get item id accept uuid") {
      for {
        found   <- assertZIO(ItemService.getItemById(ItemId(uuid0)))(equalTo(exampleItem))
        missing <-
          assertZIO(ItemService.getItemById(ItemId(uuid1)).exit)(fails(equalTo(RepositoryError.MissingEntity())))
      } yield found && missing
    }.provide(getItemMock, ItemService.layer, eventPublisherMock),
    test("update item") {
      for {
        found   <- assertZIO(ItemService.updateItem(ItemId(uuid0), updateItemInput))(
                     equalTo(Item(ItemId(uuid0), "gameboy", Money(50), ProductType.Electronics))
                   )
        missing <- ItemService.updateItem(ItemId(uuid1), updateItemInput).exit
      } yield assert(missing)(fails(equalTo(RepositoryError.MissingEntity())))
    }.provide(updateMock(updateItemInput), ItemService.layer, eventPublisherMock),
  )
