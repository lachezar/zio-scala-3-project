package se.yankov.zioapp

import zio.*
import zio.config.magnolia.descriptor
import zio.config.syntax.*
import zio.config.toKebabCase
import zio.config.typesafe.TypesafeConfig

import implementation.kafka.KafkaConfig
import implementation.postgres.DbConfig

import scala.io.Source

type ConfigEnv = DbConfig & KafkaConfig

final case class AppConfig(db: DbConfig, kafka: KafkaConfig)

object AppConfig:
  val layer: TaskLayer[ConfigEnv] =
    ZLayer(ZIO.attempt(Source.fromResource("application.conf").mkString))
      .flatMap(content =>
        val configLayer = TypesafeConfig
          .fromHoconString(content.get, descriptor[AppConfig].mapKey(toKebabCase))
          .mapError(e => new RuntimeException(e.prettyPrint()))
        configLayer.narrow(_.db) ++ configLayer.narrow(_.kafka)
      )
