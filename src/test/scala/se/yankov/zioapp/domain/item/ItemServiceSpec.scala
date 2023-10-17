package se.yankov.zioapp
package domain
package item

import zio.*
import zio.mock.Expectation.*
import zio.mock.MockSpecDefault
import zio.test.*

import domain.common.Money
import domain.events.{ EventPublisher, EventPublisherMock }
import domain.item.*

import java.util.UUID

import io.scalaland.chimney.dsl.*

object ItemServiceSpec extends MockSpecDefault:

  val uuid0           = new UUID(0, 0)
  val uuid1           = new UUID(0, 1)
  val exampleItem     = Item(ItemId(uuid0), "lego", Money(99), ProductType.Toys)
  val updateItemInput = UpdateItemInput[ValidationStatus.Validated.type]("gameboy", Money(50), ProductType.Electronics)

  val getItemMock: ULayer[ItemRepository] = ItemRepoMock.GetById(
    Assertion.equalTo(ItemId(uuid0)),
    value(exampleItem),
  ) ++ ItemRepoMock.GetById(Assertion.equalTo(ItemId(uuid1)), failure(RepositoryError.MissingEntity()))

  val getByNonExistingId: ULayer[ItemRepository] =
    ItemRepoMock.GetById(Assertion.equalTo(ItemId(uuid1)), failure(RepositoryError.MissingEntity()))

  def updateMock(data: UpdateItemInput[ValidationStatus.Validated.type]): ULayer[ItemRepository] =
    ItemRepoMock.Update(
      Assertion.hasField("id", _._1, Assertion.equalTo(exampleItem.id)),
      value(data.into[Item].withFieldConst(_.id, exampleItem.id).transform),
    ) ++ ItemRepoMock.Update(
      Assertion.hasField("id", _._1, Assertion.equalTo(ItemId(uuid1))),
      failure(RepositoryError.MissingEntity()),
    )

  def spec = suite("item service test")(
    test("get item id accept uuid") {
      for {
        found   <- assertZIO(ItemService.getItemById(ItemId(uuid0)))(Assertion.equalTo(exampleItem))
        missing <- assertZIO(ItemService.getItemById(ItemId(uuid1)).exit)(
                     Assertion.fails(Assertion.equalTo(RepositoryError.MissingEntity()))
                   )
      } yield found && missing
    }.provide(getItemMock, ItemService.layer, EventPublisherMock.optionalMockLayer),
    test("update item") {
      for {
        found   <- assertZIO(ItemService.updateItem(ItemId(uuid0), updateItemInput))(
                     Assertion.equalTo(Item(ItemId(uuid0), "gameboy", Money(50), ProductType.Electronics))
                   )
        missing <- ItemService.updateItem(ItemId(uuid1), updateItemInput).exit
      } yield assert(missing)(Assertion.fails(Assertion.equalTo(RepositoryError.MissingEntity())))
    }.provide(updateMock(updateItemInput), ItemService.layer, EventPublisherMock.optionalMockLayer),
  )
