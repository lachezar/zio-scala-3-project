package se.yankov.zioapp
package implementation
package json

import zio.json.{ JsonCodec, JsonEncoder }
import zio.json.ast.Json
import zio.json.internal.Write

given [In, Out](using opq: Opq[In, Out], codec: JsonCodec[In]): JsonCodec[Out] = codec.transform(opq.pack, _.unpack)

given JsonEncoder[Unit] = new JsonEncoder[Unit] {
  override def unsafeEncode(a: Unit, indent: Option[Int], out: Write): Unit = out.write(Json.Obj().toString)
  override def toJsonAST(a: Unit): Either[String, Json]                     = Right(Json.Obj())
}
