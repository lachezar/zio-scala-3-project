package se.yankov.zioapp
package domain
package events

import zio.*

import domain.item.Item

trait EventPublisher:
  def sendNewItemEvent(item: Item): IO[EventError, Unit]
