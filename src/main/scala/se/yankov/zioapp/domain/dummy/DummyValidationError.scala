package se.yankov.zioapp
package domain
package dummy

sealed trait DummyValidationError(message: String) extends GenericValidationError:
  override def getMessage: String = message

object DummyValidationError:
  final case class InvalidDummy(message: String = "dummy message") extends DummyValidationError(message)
