package se.yankov.zioapp
package api

import zio.*
import zio.http.*

import implementation.json.given

object InternalApi:

  val api: Http[InternalApiHandler, Nothing, Request, Response] =
    Http.collectZIO[Request] {
      case Method.DELETE -> Root / "items" =>
        ZIO.serviceWithZIO[InternalApiHandler](_.deleteAllItems).toJsonResponse.handleErrors
    } @@ requireContentType
