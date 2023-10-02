package se.yankov.zioapp
package domain
package item

import zio.*

import io.scalaland.chimney.dsl.*

final case class ItemService(itemRepo: ItemRepository):

  def addItem(input: CreateItemInput[ValidationStatus.Validated.type])
      : IO[RepositoryError.DbEx | RepositoryError.Conflict, Item] =
    zio.Random.nextUUID.flatMap { uuid =>
      itemRepo.add(input.into[Item].withFieldConst(_.id, ItemId(uuid)).transform)
    }

  def deleteItem(id: ItemId): IO[RepositoryError.DbEx | RepositoryError.MissingEntity, Unit] = itemRepo.delete(id)

  def getAllItems: IO[RepositoryError.DbEx, List[Item]] = itemRepo.getAll

  def getItemById(id: ItemId): IO[RepositoryError.DbEx | RepositoryError.MissingEntity, Item] = itemRepo.getById(id)

  def updateItem(id: ItemId, input: UpdateItemInput[ValidationStatus.Validated.type])
      : IO[RepositoryError.DbEx | RepositoryError.MissingEntity, Item] =
    itemRepo.update(id, input)

object ItemService:
  def addItem(input: CreateItemInput[ValidationStatus.Validated.type])
      : ZIO[ItemService, RepositoryError.DbEx | RepositoryError.Conflict, Item] =
    ZIO.serviceWithZIO[ItemService](_.addItem(input))

  def deleteItem(id: ItemId): ZIO[ItemService, RepositoryError.DbEx | RepositoryError.MissingEntity, Unit] =
    ZIO.serviceWithZIO[ItemService](_.deleteItem(id))

  def getAllItems(): ZIO[ItemService, RepositoryError.DbEx, List[Item]] =
    ZIO.serviceWithZIO[ItemService](_.getAllItems)

  def getItemById(id: ItemId): ZIO[ItemService, RepositoryError.DbEx | RepositoryError.MissingEntity, Item] =
    ZIO.serviceWithZIO[ItemService](_.getItemById(id))

  def updateItem(id: ItemId, input: UpdateItemInput[ValidationStatus.Validated.type])
      : ZIO[ItemService, RepositoryError.DbEx | RepositoryError.MissingEntity, Item] =
    ZIO.serviceWithZIO[ItemService](_.updateItem(id, input))

  def layer: RLayer[ItemRepository, ItemService] = ZLayer.derive[ItemService]
