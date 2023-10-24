package se.yankov.zioapp
package api

import zio.*
import zio.http.*

import domain.item.ItemId

import java.util.UUID

object PublicApi:

  val api: HttpApp[PublicApiHandler] =
    Routes(
      Method.GET / "health" -> handler(ZIO.serviceWithZIO[PublicApiHandler](_.health).toTextResponse)
    ).toHttpApp ++
      Routes(
        Method.GET / "items"                           ->
          handler(ZIO.serviceWithZIO[PublicApiHandler](_.listItems).toJsonResponse.handleErrors),
        Method.GET / "items" / pathCodec[UUID, ItemId] ->
          handler { (id: ItemId, _: Request) =>
            ZIO.serviceWithZIO[PublicApiHandler](_.getItem(id)).toJsonResponse.handleErrors
          },
      ).toHttpApp @@ requireContentType
