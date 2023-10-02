package se.yankov.zioapp
package domain
package item

import common.Money

final case class UpdateItemInput(name: String, price: Money)
