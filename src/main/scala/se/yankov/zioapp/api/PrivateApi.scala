package se.yankov.zioapp
package api

import zio.*
import zio.http.*

import domain.ValidationStatus
import domain.item.{ CreateItemInput, UpdateItemInput }
import implementation.json.ItemCodecs.given
import implementation.json.given

import java.util.UUID

object PrivateApi:

  val api: HttpApp[PrivateApiHandler] =
    Routes(
      Method.POST / "items"                ->
        handler { (req: Request) =>
          req
            .parseRequest[CreateItemInput[ValidationStatus.NonValidated.type]]
            .flatMap(input => ZIO.serviceWithZIO[PrivateApiHandler](_.createItem(req.authHeader, input)))
            .toJsonResponse
            .handleErrors
        },
      Method.GET / "items" / uuid("id")    ->
        handler { (id: UUID, req: Request) =>
          ZIO.serviceWithZIO[PrivateApiHandler](_.getItem(req.authHeader, id)).toJsonResponse.handleErrors
        },
      Method.PUT / "items" / uuid("id")    ->
        handler { (id: UUID, req: Request) =>
          req
            .parseRequest[UpdateItemInput[ValidationStatus.NonValidated.type]]
            .flatMap(input => ZIO.serviceWithZIO[PrivateApiHandler](_.updateItem(req.authHeader, id, input)))
            .toJsonResponse
            .handleErrors
        },
      Method.DELETE / "items" / uuid("id") ->
        handler { (id: UUID, req: Request) =>
          ZIO.serviceWithZIO[PrivateApiHandler](_.deleteItem(req.authHeader, id)).toJsonResponse.handleErrors
        },
    ).toHttpApp @@ requireContentType

  extension (req: Request)
    def authHeader: Option[String] = req.headers(Header.Authorization).headOption.map(_.renderedValue)
