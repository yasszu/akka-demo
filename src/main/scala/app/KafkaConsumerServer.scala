package app

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.kafka.scaladsl.{Committer, Consumer}
import akka.kafka.{CommitterSettings, ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, RunnableGraph}
import akka.util.Timeout
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

object KafkaConsumerServer {

  implicit val system: ActorSystem = ActorSystem("KafkaConsumer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val askTimeout: Timeout = Timeout(30.seconds)

  val config = system.settings.config
  val kafkaConfig = config.getConfig("akka.kafka.consumer")
  val bootstrapServers = config.getString("app.kafka.bootstrap.servers")
  val topic = config.getString("app.kafka.topic")
  val groupId = config.getString("app.kafka.group")
  val committerSettings = CommitterSettings(system)

  val consumerSettings: ConsumerSettings[String, String] =
    ConsumerSettings(kafkaConfig, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(bootstrapServers)
      .withGroupId(groupId)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val consumer: RunnableGraph[DrainingControl[Done]] =
    Consumer
      .committableSource(consumerSettings, Subscriptions.topics(topic))
      .mapAsync(10) { msg =>
        execute(msg.record.key, msg.record.value)
          .map(_ => msg.committableOffset)
      }
      .toMat(Committer.sink(committerSettings))(Keep.both)
      .mapMaterializedValue(DrainingControl.apply)

  def execute(key: String, value: String): Future[Done] = Future {
    println(s"key:$key, value:$value")
    Done
  }

  def main(args: Array[String]): Unit = {
    val control = consumer.run()

    sys.addShutdownHook {
      println("\nStopping consumer...")
      control.drainAndShutdown()
    }
  }

}
