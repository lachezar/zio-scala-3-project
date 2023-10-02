package se.yankov.zioapp
package implementation

import zio.RLayer

import domain.events.EventPublisher
import domain.item.ItemRepository
import implementation.auth.AuthService
import implementation.kafka.KafkaConfig

import javax.sql.DataSource

import postgres.*
import se.yankov.zioapp.implementation.kafka.EventPublisherImplementation

type ImplementationEnv = AuthService & ItemRepository & EventPublisher

def layer: RLayer[DbConfig & KafkaConfig, ImplementationEnv] =
  PostgresDataSource.layer >>> ItemRepositoryImplementation.layer ++ AuthService.layer ++ EventPublisherImplementation.layer
