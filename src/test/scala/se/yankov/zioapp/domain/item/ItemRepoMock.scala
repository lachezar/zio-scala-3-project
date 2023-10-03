package se.yankov.zioapp
package domain
package item

import zio.*
import zio.mock.*

object ItemRepoMock extends Mock[ItemRepository]:
  object Add     extends Effect[Item, Nothing, Item]
  object Delete  extends Effect[ItemId, Nothing, Unit]
  object GetAll  extends Effect[Unit, Nothing, List[Item]]
  object GetById extends Effect[ItemId, RepositoryError.MissingEntity, Item]
  object Update
      extends Effect[(ItemId, UpdateItemInput[ValidationStatus.Validated.type]), RepositoryError.MissingEntity, Item]

  val compose: URLayer[Proxy, ItemRepository] =
    ZLayer.fromFunction { (proxy: Proxy) =>
      new ItemRepository {
        override def add(item: Item) = proxy(Add, item)

        override def delete(id: ItemId) = proxy(Delete, id)

        override def getAll = proxy(GetAll)

        override def getById(id: ItemId) = proxy(GetById, id)

        override def update(id: ItemId, data: UpdateItemInput[ValidationStatus.Validated.type]) =
          proxy(Update, id, data)
      }
    }
