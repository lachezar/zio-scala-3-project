package se.yankov.zioapp
package implementation
package auth

import zio.*

// This could be any type of authentication mechanism - e.g. JWT

// It turns out that zio-http has a `customAuthZIO` that enables plugging in a verification function
// https://github.com/zio/zio-http/blob/de3115256ed761ad990144e50fbcdea93f306fd5/zio-http/src/main/scala/zio/http/HandlerAspect.scala#L385

final case class AuthService():

  def validateJwt(token: String): IO[AuthError, Unit] = // construct a User instead of Unit
    if (token.isBlank) ZIO.fail(AuthError())
    else ZIO.unit

object AuthService:
  val layer: ULayer[AuthService] = ZLayer.succeed(AuthService())
