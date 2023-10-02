package se.yankov.zioapp
package implementation

import zio.RLayer

import domain.item.ItemRepository
import implementation.auth.AuthService

import javax.sql.DataSource

import postgres.*

type ImplementationEnv = AuthService & ItemRepository

def layer: RLayer[DbConfig, ImplementationEnv] =
  PostgresDataSource.layer >>> ItemRepositoryImplementation.layer ++ AuthService.layer
