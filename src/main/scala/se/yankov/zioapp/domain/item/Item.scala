package se.yankov.zioapp
package domain
package item

import common.Money

final case class Item(id: ItemId, name: String, price: Money)
