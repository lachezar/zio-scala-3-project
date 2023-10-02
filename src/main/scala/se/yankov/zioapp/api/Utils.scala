package se.yankov.zioapp
package api

import zio.*
import zio.http.*
import zio.json.*

import domain.*

extension [R, E](effect: ZIO[R, E, String]) def toTextResponse: ZIO[R, E, Response] = effect.map(Response.text(_))

extension [R, E, A: JsonEncoder](effect: ZIO[R, E, A])
  def toJsonResponse: ZIO[R, E, Response] = effect.map(x => Response.json(x.toJson))

extension [R](
    effect: ZIO[
      R,
      RequestError | RepositoryError.DbEx | RepositoryError.Conflict | RepositoryError.MissingEntity |
        RepositoryError.ConversionError,
      Response,
    ]
  )
  def handleErrors: URIO[R, Response] = effect
    .tapError {
      case err: RequestError                  => ZIO.logWarning(s"RequestError: ${err.message.getOrElse("no message")}")
      case err: RepositoryError.MissingEntity => ZIO.logDebug(s"$err")
      case err: RepositoryError.DbEx          => ZIO.logError(s"RepositoryError.DbEx: ${err.ex}")
      case err                                => ZIO.logError(s"$err")
    }
    .catchAll {
      case err: RequestError                  => ZIO.succeed(ErrorResponse.badRequest)
      case err: RepositoryError.Conflict      => ZIO.succeed(ErrorResponse.conflict)
      case err: RepositoryError.MissingEntity => ZIO.succeed(ErrorResponse.notFound)
      case err                                => ZIO.succeed(ErrorResponse.internalServerError)
    }
