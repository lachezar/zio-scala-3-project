package se.yankov.zioapp
package domain
package item

import java.util.UUID

opaque type ItemId = UUID

object ItemId:
  def apply(value: UUID): ItemId         = value
  extension (id: ItemId) def value: UUID = id

  given Opq[UUID, ItemId] with
    def pack(value: UUID): ItemId           = apply(value)
    extension (id: ItemId) def unpack: UUID = id.value // TODO: check if only `id` does not work as well
