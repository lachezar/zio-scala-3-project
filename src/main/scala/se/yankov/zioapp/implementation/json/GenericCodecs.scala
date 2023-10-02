package se.yankov.zioapp
package implementation
package json

import zio.json.JsonCodec

import java.util.UUID

given jsonCodecUUID[Out](using opq: Opq[UUID, Out]): JsonCodec[Out]             = JsonCodec[UUID].transform(opq.pack, _.unpack)
given jsonCodecBigDecimal[Out](using opq: Opq[BigDecimal, Out]): JsonCodec[Out] =
  JsonCodec[BigDecimal].transform(opq.pack, _.unpack)
