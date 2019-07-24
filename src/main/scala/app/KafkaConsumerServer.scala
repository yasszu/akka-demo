package app

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.kafka.scaladsl.{Committer, Consumer}
import akka.kafka.{CommitterSettings, ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Keep
import akka.util.Timeout
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

object KafkaConsumerServer extends App {

  implicit val system: ActorSystem = ActorSystem("KafkaConsumer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val askTimeout: Timeout = Timeout(30.seconds)

  val config = system.settings.config
  val kafkaConfig = config.getConfig("akka.kafka.consumer")
  val bootstrapServers = config.getString("app.kafka.bootstrap.servers")
  val topic = config.getString("app.kafka.topic")
  val group = config.getString("app.kafka.group")
  val committerSettings = CommitterSettings(system)

  val consumerSettings =
    ConsumerSettings(kafkaConfig, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(bootstrapServers)
      .withGroupId(group)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val control =
    Consumer
      .committableSource(consumerSettings, Subscriptions.topics(topic))
      .mapAsync(1) { msg =>
        execute(msg.record.key, msg.record.value)
          .map(_ => msg.committableOffset)
      }
      .toMat(Committer.sink(committerSettings))(Keep.both)
      .mapMaterializedValue(DrainingControl.apply)
      .run()

  def execute(key: String, value: String): Future[Done] = Future {
    println(s"key:$key, value:$value")
    Done
  }

}
