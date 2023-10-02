package se.yankov.zioapp
package domain
package item

import zio.*

import RepositoryError.*

trait ItemRepository:

  def add(item: Item): IO[DbEx | Conflict, Item]

  def delete(id: ItemId): IO[DbEx | MissingEntity, Unit]

  def getAll: IO[DbEx, List[Item]]

  def getById(id: ItemId): IO[DbEx | MissingEntity, Item]

  def update(id: ItemId, data: UpdateItemInput[ValidationStatus.Validated.type]): IO[DbEx | MissingEntity, Item]

object ItemRepository:

  def add(item: Item): ZIO[ItemRepository, DbEx | Conflict, Item] =
    ZIO.serviceWithZIO[ItemRepository](_.add(item))

  def delete(id: ItemId): ZIO[ItemRepository, DbEx | MissingEntity, Unit] =
    ZIO.serviceWithZIO[ItemRepository](_.delete(id))

  def getAll: ZIO[ItemRepository, DbEx, List[Item]] =
    ZIO.serviceWithZIO[ItemRepository](_.getAll)

  def getById(id: ItemId): ZIO[ItemRepository, DbEx | MissingEntity, Item] =
    ZIO.serviceWithZIO[ItemRepository](_.getById(id))

  def update(id: ItemId, data: UpdateItemInput[ValidationStatus.Validated.type])
      : ZIO[ItemRepository, DbEx | MissingEntity, Item] =
    ZIO.serviceWithZIO[ItemRepository](_.update(id, data))
