package se.yankov.zioapp
package api

import zio.*
import zio.http.*

import implementation.json.given

object InternalApi:

  val api: HttpApp[InternalApiHandler] =
    Routes(
      Method.DELETE / "items" -> handler {
        ZIO.serviceWithZIO[InternalApiHandler](_.deleteAllItems).toJsonResponse.handleErrors
      }
    ).toHttpApp @@ requireContentType
