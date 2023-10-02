package se.yankov.zioapp
package implementation
package postgres

import zio.*

import org.flywaydb.core.Flyway

object Migration:

  def run: RIO[DbConfig, Unit] =
    ZIO.serviceWithZIO[DbConfig] { dbConfig =>
      ZIO
        .attemptBlocking(
          Flyway
            .configure
            .dataSource(dbConfig.url, dbConfig.user, dbConfig.password)
            .schemas("public")
            .baselineOnMigrate(true)
            .load
            .migrate
        )
        .tapError(err => ZIO.logError(s"Migration error: ${err.getMessage}"))
        .unit
    }
