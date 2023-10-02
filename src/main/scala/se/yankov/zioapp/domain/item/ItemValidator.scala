package se.yankov.zioapp
package domain
package item

import zio.NonEmptyChunk
import zio.prelude.Validation

import common.Money

object ItemValidator:

  def validateName(name: String): Either[ItemValidationError, String] =
    val strippedName = name.strip()
    if (strippedName.isBlank()) Left(ItemValidationError.InvalidItemName())
    else Right(strippedName)

  def validatePrice(price: Money): Either[ItemValidationError, Money] =
    if (price > Money(0)) Right(price)
    else Left(ItemValidationError.InvalidItemPrice())

  def validateProductType(productType: String): Either[ItemValidationError, String] =
    ProductType
      .valueOfEither(productType)
      .map(_ => productType)
      .left
      .map(_ => ItemValidationError.InvalidItemProductType())

  def validate(input: CreateItemInput[ValidationStatus.Unvalidated.type])
      : Either[NonEmptyChunk[ItemValidationError], CreateItemInput[ValidationStatus.Validated.type]] =
    Validation
      .validateWith(
        Validation.fromEither(validateName(input.name)),
        Validation.fromEither(validatePrice(input.price)),
        Validation.fromEither(validateProductType(input.productType)),
      )(CreateItemInput[ValidationStatus.Validated.type].apply)
      .toEither

  def validate(input: UpdateItemInput[ValidationStatus.Unvalidated.type])
      : Either[NonEmptyChunk[ItemValidationError], UpdateItemInput[ValidationStatus.Validated.type]] =
    Validation
      .validateWith(
        Validation.fromEither(validateName(input.name)),
        Validation.fromEither(validatePrice(input.price)),
        Validation.fromEither(validateProductType(input.productType)),
      )(UpdateItemInput[ValidationStatus.Validated.type].apply)
      .toEither
