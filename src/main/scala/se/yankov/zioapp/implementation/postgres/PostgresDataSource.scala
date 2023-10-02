package se.yankov.zioapp
package implementation
package postgres

import zio.*

import javax.sql.DataSource

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

object PostgresDataSource:
  val layer: RLayer[DbConfig, DataSource] =
    ZLayer(ZIO.serviceWith[DbConfig] { conf =>
      val hikariConfig = new HikariConfig()
      hikariConfig.setDriverClassName(conf.driver)
      hikariConfig.setJdbcUrl(conf.url)
      hikariConfig.setUsername(conf.user)
      hikariConfig.setPassword(conf.password)
      hikariConfig.setMaximumPoolSize(conf.maxPoolSize)
      new HikariDataSource(hikariConfig)
    })
