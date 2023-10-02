package se.yankov.zioapp
package domain
package item

sealed trait ItemValidationError(message: String) extends GenericValidationError:
  override def getMessage: String = message

object ItemValidationError:
  final case class InvalidItemName(message: String = "Name must be non-empty") extends ItemValidationError(message)
  final case class InvalidItemPrice(message: String = "Price must be greater than 0")
      extends ItemValidationError(message)
  final case class InvalidItemProductType(message: String = "Not a valid product type")
      extends ItemValidationError(message)
