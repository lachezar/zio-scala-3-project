package se.yankov.zioapp
package implementation
package postgres

import zio.*

import com.dimafeng.testcontainers.PostgreSQLContainer

val containerLayer = ZLayer.scoped(PostgresContainer.make())

val dataSourceBuilderLayer = DataSourceBuilder.layer

val dataSourceLayer = ZLayer(ZIO.service[DataSourceBuilder].map(_.dataSource))

val dbConfigLayer: URLayer[PostgreSQLContainer, DbConfig] =
  ZLayer(ZIO.service[PostgreSQLContainer].map(c => DbConfig(c.driverClassName, c.jdbcUrl, c.username, c.password, 5)))
