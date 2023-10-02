package se.yankov.zioapp
package api

import zio.http.*
import zio.json.*

@jsonDiscriminator("$type")
sealed trait ErrorResponse

object ErrorResponse:
  final case class GenericErrorResponse(message: String) extends ErrorResponse

  lazy val unauthorized: Response        =
    Response.json(GenericErrorResponse("Unauthorized").toJson).copy(status = Status.Unauthorized)
  lazy val badRequest: Response          =
    Response.json(GenericErrorResponse("Bad request").toJson).copy(status = Status.BadRequest)
  lazy val notFound: Response            = Response.json(GenericErrorResponse("Not found").toJson).copy(status = Status.NotFound)
  lazy val conflict: Response            = Response.json(GenericErrorResponse("Conflict").toJson).copy(status = Status.Conflict)
  lazy val internalServerError: Response =
    Response.json(GenericErrorResponse("Internal server error").toJson).copy(status = Status.InternalServerError)

  given JsonEncoder[GenericErrorResponse] = DeriveJsonEncoder.gen[GenericErrorResponse]
  given JsonEncoder[ErrorResponse]        = DeriveJsonEncoder.gen[ErrorResponse]
