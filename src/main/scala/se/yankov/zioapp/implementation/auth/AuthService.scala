package se.yankov.zioapp
package implementation
package auth

import zio.*

// This could be any type of authentication mechanism - e.g. JWT

final case class AuthService():

  def validateJwt(token: String): IO[AuthError, Unit] = // construct a User instead of Unit
    if (token.isBlank) ZIO.fail(AuthError())
    else ZIO.unit

object AuthService:
  val layer: ULayer[AuthService] = ZLayer.succeed(AuthService())
