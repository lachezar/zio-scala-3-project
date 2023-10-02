package se.yankov.zioapp
package implementation
package postgres

import zio.json.JsonEncoder

import java.sql.Types
import java.util.UUID

import DbContext.*
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
      @SuppressWarnings(Array("org.wartremover.warts.Null"))
      val obj = value.fold(null)(_.value)
      row.setObject(index, obj, Types.OTHER)
      row
    },
  )
