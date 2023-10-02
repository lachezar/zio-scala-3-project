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

final case class ItemEntity(id: ItemId, name: String, price: BigDecimal):
  def toDomain: Item = this.into[Item].withFieldComputed(_.price, i => Money(i.price)).transform

object ItemEntity:
  def fromDomain(item: Item) = item.into[ItemEntity].withFieldComputed(_.price, x => x.price.value).transform

final class ItemRepositoryImplementation(dataSource: DataSource) extends ItemRepository:

  import DbContext.*
  import QuillCodecs.given

  given Implicit[DataSource] = Implicit(dataSource)

  override def add(item: Item): IO[RepositoryError.DbEx | RepositoryError.Conflict, Item] =
    DbContext
      .run {
        query[ItemEntity].insertValue(lift(ItemEntity.fromDomain(item))).returning(r => r)
      }
      .implicitDS
      .mapBoth(_.toDbExOrConflict, _.toDomain)

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
      .mapBoth(_.toDbEx, _.map(_.toDomain))

  override def getById(id: ItemId): IO[RepositoryError.DbEx | RepositoryError.MissingEntity, Item] =
    DbContext
      .run(query[ItemEntity].filter(_.id == lift(id)))
      .implicitDS
      .mapError(_.toDbEx)
      .flatMap(i => ZIO.fromOption(i.headOption.map(_.toDomain)).orElseFail(RepositoryError.MissingEntity()))

  override def update(id: ItemId, data: UpdateItemInput[ValidationStatus.Validated.type])
      : IO[RepositoryError.DbEx | RepositoryError.MissingEntity, Item] =
    DbContext
      .run {
        query[ItemEntity]
          .filter(_.id == lift(id))
          .update(
            _.name  -> lift(data.name),
            _.price -> lift(data.price.value),
          )
          .returningMany(r => r)
      }
      .implicitDS
      .mapError(_.toDbEx)
      .flatMap(i => ZIO.fromOption(i.headOption.map(_.toDomain)).orElseFail(RepositoryError.MissingEntity()))

object ItemRepositoryImplementation:
  val layer: URLayer[DataSource, ItemRepository] = ZLayer.derive[ItemRepositoryImplementation]
