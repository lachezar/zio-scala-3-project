package se.yankov.zioapp
package api

import zio.json.JsonEncoder

final case class ValidationError(`type`: String, message: String) derives JsonEncoder
