package se.yankov.zioapp
package domain
package item

import zio.*

import io.scalaland.chimney.dsl.*

final class InMemoryItemRepository(storeRef: Ref[Map[ItemId, Item]]) extends ItemRepository:

  override def add(item: Item)
      : IO[RepositoryError.DbEx | RepositoryError.Conflict | RepositoryError.ConversionError, Item] =
    storeRef.update(store => store + (item.id -> item)) *> ZIO.succeed(item)

  override def delete(id: ItemId): IO[RepositoryError.DbEx | RepositoryError.MissingEntity, Unit] =
    storeRef
      .modify { store =>
        if (!store.contains(id)) (None, store)
        else (Some(()), store.removed(id))
      }
      .someOrFail(RepositoryError.MissingEntity())

  override def getAll: IO[RepositoryError.DbEx, List[Item]] =
    storeRef.get.map { store =>
      store.toList.map(_._2)
    }

  override def getById(id: ItemId): IO[RepositoryError.DbEx | RepositoryError.MissingEntity, Item] =
    for {
      store <- storeRef.get
      item  <- ZIO.getOrFailWith(RepositoryError.MissingEntity())(store.get(id))
    } yield item

  override def update(id: ItemId, data: UpdateItemInput[ValidationStatus.Validated.type])
      : IO[RepositoryError.DbEx | RepositoryError.MissingEntity | RepositoryError.ConversionError, Item] =
    storeRef
      .modify { store =>
        if (!store.contains(id)) (None, store)
        else {
          val updatedItem = data.into[Item].withFieldConst(_.id, id).transform
          (Some(updatedItem), store.updated(id, updatedItem))
        }
      }
      .someOrFail(RepositoryError.MissingEntity())

object InMemoryItemRepository:
  val layer: ULayer[ItemRepository] = ZLayer(Ref.make(Map.empty[ItemId, Item]).map(InMemoryItemRepository(_)))
