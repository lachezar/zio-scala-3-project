package se.yankov.zioapp
package implementation
package postgres

import java.util.UUID

import DbContext.*
import java.sql.Types
import zio.json.JsonEncoder
import io.getquill.JsonbValue

object QuillCodecs:

  given [Out](using opq: Opq[UUID, Out]): Decoder[Out] =
    decoder(row => index => opq.pack(UUID.fromString(row.getObject(index).toString)))
  given [Out](using opq: Opq[UUID, Out]): Encoder[Out] =
    encoder(Types.OTHER, (index, value, row) => row.setObject(index, opq.unpack(value), Types.OTHER))

  // Fix for jsonb columns that can be null https://github.com/zio/zio-protoquill/issues/283#issuecomment-1627799047
  given [T]: Encoder[Option[JsonbValue[T]]] = JdbcEncoder[Option[JsonbValue[T]]](
    Types.OTHER,
    (index, value, row, _) => {
      if (value.isEmpty) row.setObject(index, null, Types.OTHER)
      else row.setObject(index, value.get.value, Types.OTHER)
      row
    },
  )
