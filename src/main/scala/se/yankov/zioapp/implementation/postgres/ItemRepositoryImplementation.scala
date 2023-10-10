package se.yankov.zioapp
package implementation
package postgres

import zio.{ IO, URLayer, ZIO, ZLayer }

import domain.*
import domain.common.*
import domain.item.*

import java.sql.SQLException
import javax.sql.DataSource

import io.getquill.*
import io.getquill.context.ZioJdbc.*
import io.getquill.context.qzio.ImplicitSyntax.Implicit
import io.scalaland.chimney.dsl.*

final case class ItemEntity(id: ItemId, name: String, price: BigDecimal, productType: String):
  def toDomain: Option[Item] =
    ProductType
      .valueOfOption(productType)
      .map(pt =>
        this.into[Item].withFieldComputed(_.price, i => Money(i.price)).withFieldConst(_.productType, pt).transform
      )

object ItemEntity:
  def fromDomain(item: Item) = item
    .into[ItemEntity]
    .withFieldComputed(_.price, x => x.price.value)
    .withFieldComputed(_.productType, _.productType.toString)
    .transform

final class ItemRepositoryImplementation(dataSource: DataSource) extends ItemRepository:

  import DbContext.*
  import QuillCodecs.given

  given Implicit[DataSource] = Implicit(dataSource)

  override def add(item: Item)
      : IO[RepositoryError.DbEx | RepositoryError.Conflict | RepositoryError.ConversionError, Item] =
    DbContext
      .run {
        query[ItemEntity].insertValue(lift(ItemEntity.fromDomain(item))).returning(r => r)
      }
      .implicitDS
      .mapError(_.toDbExOrConflict)
      .flatMap(i => ZIO.getOrFailWith(RepositoryError.ConversionError())(i.toDomain))

  override def delete(id: ItemId): IO[RepositoryError.DbEx | RepositoryError.MissingEntity, Unit] =
    DbContext
      .run(query[ItemEntity].filter(_.id == lift(id)).delete)
      .implicitDS
      .mapError(_.toDbEx)
      .flatMap {
        case 0 => ZIO.fail(RepositoryError.MissingEntity())
        case _ => ZIO.unit
      }

  override def getAll: IO[RepositoryError.DbEx, List[Item]] =
    DbContext
      .run(query[ItemEntity])
      .implicitDS
      .mapBoth(_.toDbEx, _.flatMap(_.toDomain))

  override def getById(id: ItemId)
      : IO[RepositoryError.DbEx | RepositoryError.MissingEntity | RepositoryError.ConversionError, Item] =
    DbContext
      .run(query[ItemEntity].filter(_.id == lift(id)))
      .implicitDS
      .mapError(_.toDbEx)
      .flatMap {
        case Nil    => ZIO.fail(RepositoryError.MissingEntity())
        case i :: _ => ZIO.getOrFailWith(RepositoryError.ConversionError())(i.toDomain)
      }

  override def update(id: ItemId, data: UpdateItemInput[ValidationStatus.Validated.type])
      : IO[RepositoryError.DbEx | RepositoryError.MissingEntity | RepositoryError.ConversionError, Item] =
    DbContext
      .run {
        query[ItemEntity]
          .filter(_.id == lift(id))
          .update(
            _.name        -> lift(data.name),
            _.price       -> lift(data.price.value),
            _.productType -> lift(data.productType.toString),
          )
          .returningMany(r => r)
      }
      .implicitDS
      .mapError(_.toDbEx)
      .flatMap {
        case Nil    => ZIO.fail(RepositoryError.MissingEntity())
        case i :: _ => ZIO.getOrFailWith(RepositoryError.ConversionError())(i.toDomain)
      }

object ItemRepositoryImplementation:
  val layer: URLayer[DataSource, ItemRepository] = ZLayer.derive[ItemRepositoryImplementation]
