package se.yankov.zioapp
package api
package item

import zio.json.*

import domain.common.Money
import domain.item.{ Item, ItemId, ProductType }
import implementation.json.ItemCodecs.given
import implementation.json.given

import io.scalaland.chimney.dsl.*

final case class ItemResult(id: ItemId, name: String, price: Money, productType: ProductType) derives JsonCodec

object ItemResult:
  def fromDomain(item: Item): ItemResult = item.transformInto[ItemResult]
