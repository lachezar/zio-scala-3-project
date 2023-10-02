package se.yankov.zioapp
package domain

sealed trait ValidationStatus

object ValidationStatus:
  case object Validated   extends ValidationStatus
  case object Unvalidated extends ValidationStatus