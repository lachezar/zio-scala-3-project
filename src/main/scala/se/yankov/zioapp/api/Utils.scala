package se.yankov.zioapp
package api

import zio.*
import zio.http.*
import zio.http.codec.*
import zio.json.{ uuid => _, * }

import domain.*
import domain.events.EventError
import implementation.auth.AuthError
import implementation.json.JsonDecodingError

import java.util.UUID

extension (request: Request)
  def parseRequest[A: JsonDecoder]: IO[RequestError | JsonDecodingError, A] = request
    .body
    .asString
    .mapError(err => RequestError(Some(err.getMessage)))
    .flatMap(input => ZIO.fromEither(input.fromJson[A]).mapError(JsonDecodingError(_)))

extension [R, E](effect: URIO[R, String]) def toTextResponse: URIO[R, Response] = effect.map(Response.text(_))

extension [R, E, A: JsonEncoder](effect: ZIO[R, E, A])
  def toJsonResponse: ZIO[R, E, Response] = effect.map(x => Response.json(x.toJson))

extension [R](
    effect: ZIO[
      R,
      AuthError | RequestError | JsonDecodingError | RepositoryError.DbEx | RepositoryError.Conflict |
        RepositoryError.MissingEntity | RepositoryError.ConversionError | EventError |
        NonEmptyChunk[GenericValidationError],
      Response,
    ]
  )
  def handleErrors: URIO[R, Response] = effect
    .tapError {
      case err: AuthError                             => ZIO.logDebug(s"AuthError")
      case err: RequestError                          => ZIO.logWarning(s"RequestError: ${err.message.getOrElse("no message")}")
      case err: JsonDecodingError                     => ZIO.logWarning(s"JsonDecodingError: ${err.message}")
      case err: NonEmptyChunk[GenericValidationError] => ZIO.logDebug(s"ValidationErrors")
      case err: RepositoryError.MissingEntity         => ZIO.logDebug(s"$err")
      case err: RepositoryError.DbEx                  => ZIO.logError(s"RepositoryError.DbEx: ${err.ex}")
      case err                                        => ZIO.logError(s"$err")
    }
    .catchAll {
      case err: AuthError                             => ZIO.succeed(ErrorResponse.unauthorized)
      case err: NonEmptyChunk[GenericValidationError] => ZIO.succeed(ErrorResponse.fromValidationErrors(err))
      case err: (RequestError | JsonDecodingError)    => ZIO.succeed(ErrorResponse.badRequest)
      case err: RepositoryError.Conflict              => ZIO.succeed(ErrorResponse.conflict)
      case err: RepositoryError.MissingEntity         => ZIO.succeed(ErrorResponse.notFound)
      case err                                        => ZIO.succeed(ErrorResponse.internalServerError)
    }

val requireContentType: HandlerAspect[Any, Unit] =
  HandlerAspect
    .Allow(())
    .apply(req => req.headers.get(Header.ContentType).exists(_.mediaType == MediaType.application.json))

def pathCodec[In, Out](using opq: Opq[In, Out], codec: PathCodec[In]): PathCodec[Out] =
  codec.transform[Out](opq.pack(_))(opq.unpack)

given PathCodec[UUID] = uuid("id")
