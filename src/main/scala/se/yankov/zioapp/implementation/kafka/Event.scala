package se.yankov.zioapp
package implementation
package kafka

import zio.json.{ jsonDiscriminator, JsonCodec }

import domain.common.Money
import domain.item.ProductType
import implementation.json.ItemCodecs.given
import implementation.json.given

import java.time.Instant

@jsonDiscriminator("type")
sealed trait Event derives JsonCodec

object Event:
  final case class NewItemAdded(name: String, price: Money, productType: ProductType, timestamp: Instant) extends Event
      derives JsonCodec
