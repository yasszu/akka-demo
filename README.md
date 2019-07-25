# Akka Demo

## Start Kafka
See https://kafka.apache.org/quickstart

Start docker container
```
$ docker-compose up -d
```

Login kafka container
```
$  docker-compose exec kafka sh
```

Create topic
```
$ cd /opt/kafka
$ bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic test
$ bin/kafka-topics.sh --list --bootstrap-server localhost:9092
test
```

Send message
```
$ cd /opt/kafka
$ bin/kafka-console-producer.sh --broker-list localhost:9092 --topic Timeline
> message1
> message2
```

## Start application

```
$ sbt run
```
