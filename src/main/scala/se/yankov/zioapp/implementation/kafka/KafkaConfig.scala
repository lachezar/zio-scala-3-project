package se.yankov.zioapp
package implementation
package kafka

final case class KafkaConfig(bootstrapServers: List[String], eventTopic: String)
