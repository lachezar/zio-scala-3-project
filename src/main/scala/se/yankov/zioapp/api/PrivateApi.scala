package se.yankov.zioapp
package api

import zio.*
import zio.http.*

import domain.ValidationStatus
import domain.item.{ CreateItemInput, UpdateItemInput }
import implementation.json.ItemCodecs.given
import implementation.json.given

object PrivateApi:

  val api: Http[PrivateApiHandler, Nothing, Request, Response] =
    Http.collectZIO[Request] {
      case req @ Method.POST -> Root / "items"        =>
        req
          .parseRequest[CreateItemInput[ValidationStatus.NonValidated.type]]
          .flatMap(input => ZIO.serviceWithZIO[PrivateApiHandler](_.createItem(req.authHeader, input)))
          .toJsonResponse
          .handleErrors
      case req @ Method.GET -> Root / "items" / id    =>
        ZIO.serviceWithZIO[PrivateApiHandler](_.getItem(req.authHeader, id)).toJsonResponse.handleErrors
      case req @ Method.PUT -> Root / "items" / id    =>
        req
          .parseRequest[UpdateItemInput[ValidationStatus.NonValidated.type]]
          .flatMap(input => ZIO.serviceWithZIO[PrivateApiHandler](_.updateItem(req.authHeader, id, input)))
          .toJsonResponse
          .handleErrors
      case req @ Method.DELETE -> Root / "items" / id =>
        ZIO.serviceWithZIO[PrivateApiHandler](_.deleteItem(req.authHeader, id)).toJsonResponse.handleErrors
    } @@ requireContentType

  extension (req: Request)
    def authHeader: Option[String] = req.headers(Header.Authorization).headOption.map(_.renderedValue)
