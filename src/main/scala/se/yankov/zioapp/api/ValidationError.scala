package se.yankov.zioapp
package api

import zio.json.{ DeriveJsonEncoder, JsonEncoder }

import domain.item.ItemValidationError

final case class ValidationError(`type`: String, message: String)

object ValidationError:
  given JsonEncoder[ValidationError] = DeriveJsonEncoder.gen[ValidationError]
