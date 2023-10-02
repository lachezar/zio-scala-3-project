package se.yankov.zioapp
package domain
package item

import zio.*

import domain.events.*

import io.scalaland.chimney.dsl.*

final case class ItemService(itemRepo: ItemRepository, eventPublisher: EventPublisher):

  def addItem(input: CreateItemInput[ValidationStatus.Validated.type])
      : IO[RepositoryError.DbEx | RepositoryError.Conflict | RepositoryError.ConversionError | EventError, Item] =
    for {
      uuid <- zio.Random.nextUUID
      item <- itemRepo.add(
                input
                  .into[Item]
                  .withFieldConst(_.id, ItemId(uuid))
                  .withFieldComputed(_.productType, i => ProductType.valueOf(i.productType))
                  .transform
              )
      _    <- eventPublisher.sendNewItemEvent(item)
    } yield item

  def deleteItem(id: ItemId): IO[RepositoryError.DbEx | RepositoryError.MissingEntity, Unit] = itemRepo.delete(id)

  def getAllItems: IO[RepositoryError.DbEx, List[Item]] = itemRepo.getAll

  def getItemById(id: ItemId)
      : IO[RepositoryError.DbEx | RepositoryError.MissingEntity | RepositoryError.ConversionError, Item] =
    itemRepo.getById(id)

  def updateItem(id: ItemId, input: UpdateItemInput[ValidationStatus.Validated.type])
      : IO[RepositoryError.DbEx | RepositoryError.MissingEntity | RepositoryError.ConversionError, Item] =
    itemRepo.update(id, input)

object ItemService:
  def addItem(input: CreateItemInput[ValidationStatus.Validated.type])
      : ZIO[ItemService, RepositoryError.DbEx | RepositoryError.Conflict | RepositoryError.ConversionError | EventError, Item] =
    ZIO.serviceWithZIO[ItemService](_.addItem(input))

  def deleteItem(id: ItemId): ZIO[ItemService, RepositoryError.DbEx | RepositoryError.MissingEntity, Unit] =
    ZIO.serviceWithZIO[ItemService](_.deleteItem(id))

  def getAllItems(): ZIO[ItemService, RepositoryError.DbEx, List[Item]] =
    ZIO.serviceWithZIO[ItemService](_.getAllItems)

  def getItemById(id: ItemId)
      : ZIO[ItemService, RepositoryError.DbEx | RepositoryError.MissingEntity | RepositoryError.ConversionError, Item] =
    ZIO.serviceWithZIO[ItemService](_.getItemById(id))

  def updateItem(id: ItemId, input: UpdateItemInput[ValidationStatus.Validated.type])
      : ZIO[ItemService, RepositoryError.DbEx | RepositoryError.MissingEntity | RepositoryError.ConversionError, Item] =
    ZIO.serviceWithZIO[ItemService](_.updateItem(id, input))

  def layer: RLayer[ItemRepository & EventPublisher, ItemService] = ZLayer.derive[ItemService]
