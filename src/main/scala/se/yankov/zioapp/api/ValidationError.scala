package se.yankov.zioapp
package api

import zio.json.JsonEncoder

import domain.item.ItemValidationError

final case class ValidationError(`type`: String, message: String) derives JsonEncoder
