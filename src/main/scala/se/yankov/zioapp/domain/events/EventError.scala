package se.yankov.zioapp
package domain
package events

final case class EventError(ex: Throwable)
