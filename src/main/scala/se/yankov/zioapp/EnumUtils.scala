package se.yankov.zioapp

import reflect.Selectable.reflectiveSelectable
import scala.util.Try

extension [E](e: { def valueOf(value: String): E })
  def valueOfEither(value: String): Either[Throwable, E] = Try(e.valueOf(value)).toEither
  def valueOfOption(value: String): Option[E]            = valueOfEither(value).toOption
