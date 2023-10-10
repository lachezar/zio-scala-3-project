package se.yankov.zioapp
package implementation
package kafka

import zio.*
import zio.json.*
import zio.kafka.*
import zio.kafka.producer.*
import zio.kafka.serde.*

import domain.events.*
import domain.item.Item

final case class EventPublisherImplementation(kafkaConfig: KafkaConfig, producer: Producer) extends EventPublisher:

  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  override def sendNewItemEvent(item: Item): IO[EventError, Unit] =
    zio
      .Clock
      .instant
      .map(now => Event.NewItemAdded(item.name, item.price, item.productType, now))
      .flatMap(event => producer.produce(kafkaConfig.eventTopic, null, event.toJson, Serde.string, Serde.string))
      .mapError(EventError(_))
      .unit

object EventPublisherImplementation:
  val layer: RLayer[KafkaConfig, EventPublisher] = ZLayer.scoped(
    ZIO.serviceWithZIO[KafkaConfig](config => Producer.make(ProducerSettings(config.bootstrapServers)))
  ) >>> ZLayer.derive[EventPublisherImplementation]
