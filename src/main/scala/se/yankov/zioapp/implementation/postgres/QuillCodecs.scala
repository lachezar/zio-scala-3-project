package se.yankov.zioapp
package implementation
package postgres

import java.sql.Types

import DbContext.*
import io.getquill.{ JsonbValue, MappedEncoding }

object QuillCodecs:

  given [In, Out](using opq: Opq[In, Out]): MappedEncoding[Out, In] = MappedEncoding[Out, In](_.unpack)
  given [In, Out](using opq: Opq[In, Out]): MappedEncoding[In, Out] = MappedEncoding[In, Out](opq.pack(_))

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
