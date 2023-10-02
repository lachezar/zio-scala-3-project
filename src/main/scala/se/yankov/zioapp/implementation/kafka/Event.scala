package se.yankov.zioapp
package implementation
package kafka

import zio.json.{ jsonDiscriminator, DeriveJsonCodec, JsonCodec }

import domain.common.Money
import domain.item.ProductType
import implementation.json.ItemCodecs.given
import implementation.json.given

import java.time.Instant

@jsonDiscriminator("type")
sealed trait Event

object Event:
  final case class NewItemAdded(name: String, price: Money, productType: ProductType, timestamp: Instant) extends Event

  given JsonCodec[Event]        = DeriveJsonCodec.gen[Event]
  given JsonCodec[NewItemAdded] = DeriveJsonCodec.gen[NewItemAdded]
