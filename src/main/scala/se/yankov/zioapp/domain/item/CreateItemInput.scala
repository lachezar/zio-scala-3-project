package se.yankov.zioapp
package domain
package item

import common.Money

final case class CreateItemInput[V <: ValidationStatus](name: String, price: Money, productType: ProductType)
