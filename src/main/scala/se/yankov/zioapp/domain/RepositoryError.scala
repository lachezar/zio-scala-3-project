package se.yankov.zioapp
package domain

object RepositoryError:
  final case class DbEx(ex: Throwable)
  final case class Conflict()
  final case class MissingEntity()
  final case class ConversionError()
