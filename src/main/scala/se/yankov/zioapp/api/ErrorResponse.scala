package se.yankov.zioapp
package api

import zio.NonEmptyChunk
import zio.http.*
import zio.json.*

import domain.GenericValidationError

@jsonDiscriminator("$type")
sealed trait ErrorResponse derives JsonEncoder

object ErrorResponse:
  final case class GenericErrorResponse(message: String)                            extends ErrorResponse derives JsonEncoder
  final case class ValidationErrorsResponse(errors: NonEmptyChunk[ValidationError]) extends ErrorResponse
      derives JsonEncoder

  def fromValidationErrors(errors: NonEmptyChunk[GenericValidationError]): Response =
    Response
      .json(ValidationErrorsResponse(errors.map(e => ValidationError(e.getClass.getSimpleName, e.getMessage))).toJson)
      .copy(status = Status.BadRequest)

  lazy val unauthorized: Response        =
    Response.json(GenericErrorResponse("Unauthorized").toJson).copy(status = Status.Unauthorized)
  lazy val badRequest: Response          =
    Response.json(GenericErrorResponse("Bad request").toJson).copy(status = Status.BadRequest)
  lazy val notFound: Response            = Response.json(GenericErrorResponse("Not found").toJson).copy(status = Status.NotFound)
  lazy val conflict: Response            = Response.json(GenericErrorResponse("Conflict").toJson).copy(status = Status.Conflict)
  lazy val internalServerError: Response =
    Response.json(GenericErrorResponse("Internal server error").toJson).copy(status = Status.InternalServerError)
