package se.yankov.zioapp
package implementation
package postgres

import zio.*

import javax.sql.DataSource

import com.dimafeng.testcontainers.PostgreSQLContainer
import io.getquill.context.ZioJdbc.DataSourceLayer
import org.postgresql.ds.PGSimpleDataSource

final class DataSourceBuilder(container: PostgreSQLContainer):

  val dataSource: DataSource =
    val ds = new PGSimpleDataSource()
    ds.setUrl(container.jdbcUrl)
    ds.setUser(container.username)
    ds.setPassword(container.password)
    ds

object DataSourceBuilder:
  val layer: URLayer[PostgreSQLContainer, DataSourceBuilder] =
    ZLayer(ZIO.service[PostgreSQLContainer].map(DataSourceBuilder(_)))
