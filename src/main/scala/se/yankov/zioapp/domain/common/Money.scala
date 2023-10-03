package se.yankov.zioapp
package domain
package common

opaque type Money = BigDecimal

object Money:
  def apply(value: BigDecimal): Money = value
  def apply(value: Int): Money        = BigDecimal(value)

  extension (money: Money)
    def value: BigDecimal       = money
    def <(that: Money): Boolean = value < that.value
    def >(that: Money): Boolean = value > that.value

  given Opq[BigDecimal, Money] with
    def pack(value: BigDecimal): Money              = value
    extension (value: Money) def unpack: BigDecimal = value
