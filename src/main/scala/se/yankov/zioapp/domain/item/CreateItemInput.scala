package se.yankov.zioapp
package domain
package item

import common.Money

final case class CreateItemInput(name: String, price: Money)
