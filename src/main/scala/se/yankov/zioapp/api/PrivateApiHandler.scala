package se.yankov.zioapp
package api

import zio.*

import api.item.*
import domain.*
import domain.events.EventError
import domain.item.*
import implementation.auth.*

final case class PrivateApiHandler(authService: AuthService, itemService: ItemService):

  def createItem(authHeader: Option[String], input: CreateItemInput[ValidationStatus.NonValidated.type])
      : IO[AuthError | RepositoryError.DbEx | RepositoryError.Conflict | RepositoryError.ConversionError | EventError | NonEmptyChunk[ItemValidationError], ItemResult] =
    for {
      _              <- authService.validateJwt(authHeader.getOrElse(""))
      validatedInput <- ZIO.fromEither(ItemValidator.validate(input))
      item           <- itemService.addItem(validatedInput)
    } yield ItemResult.fromDomain(item)

  def updateItem(authHeader: Option[String], id: ItemId, input: UpdateItemInput[ValidationStatus.NonValidated.type])
      : IO[
        AuthError | RequestError | RepositoryError.DbEx | RepositoryError.MissingEntity | RepositoryError.ConversionError | NonEmptyChunk[ItemValidationError],
        ItemResult,
      ] =
    for {
      _              <- authService.validateJwt(authHeader.getOrElse(""))
      validatedInput <- ZIO.fromEither(ItemValidator.validate(input))
      item           <- itemService.updateItem(id, validatedInput)
    } yield ItemResult.fromDomain(item)

  def deleteItem(authHeader: Option[String], id: ItemId)
      : IO[AuthError | RequestError | RepositoryError.DbEx | RepositoryError.MissingEntity, Unit] =
    authService.validateJwt(authHeader.getOrElse("")) *> itemService.deleteItem(id)

  def getItem(authHeader: Option[String], id: ItemId)
      : IO[AuthError | RepositoryError.DbEx | RepositoryError.MissingEntity | RepositoryError.ConversionError | RequestError, ItemResult] =
    authService.validateJwt(authHeader.getOrElse("")) *> itemService.getItemById(id).map(ItemResult.fromDomain(_))

object PrivateApiHandler:
  val layer: RLayer[AuthService & ItemService, PrivateApiHandler] = ZLayer.derive[PrivateApiHandler]
