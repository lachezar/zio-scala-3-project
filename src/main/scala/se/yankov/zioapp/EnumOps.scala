package se.yankov.zioapp

trait EnumOps[E <: scala.reflect.Enum]:
  def values: Array[E]
  def valueOfOption(value: String): Option[E]         = values.find(_.toString == value)
  def valueOfEither(value: String): Either[String, E] = valueOfOption(value).toRight(s"Enum case not found: $value")
