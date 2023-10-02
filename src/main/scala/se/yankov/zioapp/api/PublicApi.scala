package se.yankov.zioapp
package api

import zio.*
import zio.http.*

import api.PublicApiHandler

object PublicApi:

  val api: Http[PublicApiHandler, Nothing, Request, Response] =
    Http.collectZIO[Request] {
      case Method.GET -> Root / "health"     => ZIO.serviceWithZIO[PublicApiHandler](_.health).toTextResponse
      case Method.GET -> Root / "items"      =>
        ZIO.serviceWithZIO[PublicApiHandler](_.listItems).toJsonResponse.handleErrors
      case Method.GET -> Root / "items" / id =>
        ZIO.serviceWithZIO[PublicApiHandler](_.getItem(id)).toJsonResponse.handleErrors
    }
