package se.yankov.zioapp
package api

import zio.*
import zio.http.*

import domain.ValidationStatus
import domain.item.{ CreateItemInput, ItemId, UpdateItemInput }
import implementation.json.ItemCodecs.given
import implementation.json.given

import java.util.UUID

object PrivateApi:

  val api: Routes[PrivateApiHandler, Nothing] =
    Routes(
      Method.POST / "items"                             ->
        handler { (req: Request) =>
          req
            .parseRequest[CreateItemInput[ValidationStatus.NonValidated.type]]
            .flatMap(input => ZIO.serviceWithZIO[PrivateApiHandler](_.createItem(req.authHeader, input)))
            .toJsonResponse
            .handleErrors
        },
      Method.GET / "items" / pathCodec[UUID, ItemId]    ->
        handler { (id: ItemId, req: Request) =>
          ZIO.serviceWithZIO[PrivateApiHandler](_.getItem(req.authHeader, id)).toJsonResponse.handleErrors
        },
      Method.PUT / "items" / pathCodec[UUID, ItemId]    ->
        handler { (id: ItemId, req: Request) =>
          req
            .parseRequest[UpdateItemInput[ValidationStatus.NonValidated.type]]
            .flatMap(input => ZIO.serviceWithZIO[PrivateApiHandler](_.updateItem(req.authHeader, id, input)))
            .toJsonResponse
            .handleErrors
        },
      Method.DELETE / "items" / pathCodec[UUID, ItemId] ->
        handler { (id: ItemId, req: Request) =>
          ZIO.serviceWithZIO[PrivateApiHandler](_.deleteItem(req.authHeader, id)).toJsonResponse.handleErrors
        },
    ) @@ requireContentType

  extension (req: Request)
    def authHeader: Option[String] = req.headers(Header.Authorization).headOption.map(_.renderedValue)
