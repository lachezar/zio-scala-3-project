package se.yankov.zioapp
package api

import zio.*

import domain.RepositoryError
import domain.item.ItemService

final case class InternalApiHandler(itemService: ItemService):

  def deleteAllItems: IO[RepositoryError.DbEx | RepositoryError.MissingEntity, Unit] =
    itemService.getAllItems.flatMap(items => ZIO.foreachDiscard(items)(i => itemService.deleteItem(i.id)))

object InternalApiHandler:
  val layer: RLayer[ItemService, InternalApiHandler] = ZLayer.derive[InternalApiHandler]
