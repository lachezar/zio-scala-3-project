package se.yankov.zioapp
package domain
package item

import common.Money

final case class UpdateItemInput[V <: ValidationStatus](name: String, price: Money, productType: String)
