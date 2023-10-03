package se.yankov.zioapp
package implementation
package postgres

import zio.*

import com.dimafeng.testcontainers.PostgreSQLContainer
import org.postgresql.ds.PGSimpleDataSource
import org.testcontainers.utility.DockerImageName

val containerLayer: TaskLayer[PostgreSQLContainer] = ZLayer.scoped(
  ZIO.acquireRelease {
    ZIO.attempt {
      val imageName = "postgres:alpine"
      val c         = new PostgreSQLContainer(
        dockerImageNameOverride = Option(imageName).map(DockerImageName.parse)
      )
      c.start()
      c
    }
  } { container =>
    ZIO.attempt(container.stop()).orDie
  }
)

val dataSourceLayer: URLayer[PostgreSQLContainer, PGSimpleDataSource] = ZLayer(
  ZIO.service[PostgreSQLContainer].map { container =>
    val ds = new PGSimpleDataSource()
    ds.setUrl(container.jdbcUrl)
    ds.setUser(container.username)
    ds.setPassword(container.password)
    ds
  }
)

val dbConfigLayer: URLayer[PostgreSQLContainer, DbConfig] =
  ZLayer(ZIO.service[PostgreSQLContainer].map(c => DbConfig(c.driverClassName, c.jdbcUrl, c.username, c.password, 5)))
