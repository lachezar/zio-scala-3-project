package se.yankov.zioapp
package implementation
package json

import zio.json.*

import domain.ValidationStatus
import domain.item.{ CreateItemInput, ProductType, UpdateItemInput }

object ItemCodecs:
  given JsonCodec[CreateItemInput[ValidationStatus.NonValidated.type]] =
    DeriveJsonCodec.gen[CreateItemInput[ValidationStatus.NonValidated.type]]
  given JsonCodec[UpdateItemInput[ValidationStatus.NonValidated.type]] =
    DeriveJsonCodec.gen[UpdateItemInput[ValidationStatus.NonValidated.type]]
  given JsonCodec[ProductType]                                         =
    JsonCodec[String].transformOrFail(ProductType.valueOfEither(_).left.map(_.getMessage), _.toString)
