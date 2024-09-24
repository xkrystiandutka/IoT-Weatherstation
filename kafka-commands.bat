@echo off
:: Start Zookeeper
D:\kafka\bin\windows\zookeeper-server-start.bat D:\kafka\config\zookeeper.properties

:: Start Kafka broker
D:\kafka\bin\windows\kafka-server-start.bat D:\kafka\config\server.properties

:: Start Kafka Connect in standalone mode
D:\kafka\bin\windows\connect-standalone.bat D:\kafka\config\connect-standalone.properties D:\kafka\config\connect-file-sink.properties

:: Stop Zookeeper
D:\kafka\bin\windows\zookeeper-server-stop.bat D:\kafka\config\zookeeper.properties

:: Stop Kafka broker
D:\kafka\bin\windows\kafka-server-stop.bat D:\kafka\config\server.properties

:: Create Kafka topic
D:\kafka\bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic real-time-data

:: Consume from real-time-data topic
D:\kafka\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic real-time-data

:: Consume from processed-real-time-data topic
D:\kafka\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic processed-real-time-data
