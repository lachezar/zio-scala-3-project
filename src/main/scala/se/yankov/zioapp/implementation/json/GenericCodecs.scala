package se.yankov.zioapp
package implementation
package json

import zio.json.{ JsonCodec, JsonEncoder }
import zio.json.ast.Json
import zio.json.internal.Write

import java.util.UUID

given jsonCodecUUID[Out](using opq: Opq[UUID, Out]): JsonCodec[Out]             = JsonCodec[UUID].transform(opq.pack, _.unpack)
given jsonCodecBigDecimal[Out](using opq: Opq[BigDecimal, Out]): JsonCodec[Out] =
  JsonCodec[BigDecimal].transform(opq.pack, _.unpack)

given JsonEncoder[Unit] = new JsonEncoder[Unit] {
  override def unsafeEncode(a: Unit, indent: Option[Int], out: Write): Unit = out.write(Json.Obj().toString)
  override def toJsonAST(a: Unit): Either[String, Json]                     = Right(Json.Obj())
}
