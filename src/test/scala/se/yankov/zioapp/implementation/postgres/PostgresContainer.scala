package se.yankov.zioapp
package implementation
package postgres

import zio.*

import com.dimafeng.testcontainers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

object PostgresContainer:

  def make(imageName: String = "postgres:alpine"): RIO[Scope, PostgreSQLContainer] =
    ZIO.acquireRelease {
      ZIO.attempt {
        val c = new PostgreSQLContainer(
          dockerImageNameOverride = Option(imageName).map(DockerImageName.parse)
        )
        c.start()
        c
      }
    } { container =>
      ZIO.attempt(container.stop()).orDie
    }
