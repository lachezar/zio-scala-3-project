package se.yankov.zioapp
package implementation

import zio.RLayer

import domain.item.ItemRepository

import javax.sql.DataSource

import postgres.*

type ImplementationEnv = ItemRepository

def layer: RLayer[DbConfig, ImplementationEnv] = PostgresDataSource.layer >>> ItemRepositoryImplementation.layer
