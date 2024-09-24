package org.example

import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, StreamsConfig}
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.common.serialization.Serdes
import java.io.{FileWriter, PrintWriter, IOException}
import java.util.Properties

object RealTimeDataProcessing {

  def main(args: Array[String]): Unit = {
    val props: Properties = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "real-time-data-processing")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass.getName)
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass.getName)

    val builder: StreamsBuilder = new StreamsBuilder()

    val inputStream: KStream[String, String] = builder.stream("real-time-data")

    val processedStream: KStream[String, String] = inputStream.mapValues(value => {
      if (value.isEmpty) {
        System.err.println("An empty string was received")
        null
      } else {
        val parts: Array[String] = value.split(",")
        if (parts.length != 3) { // Expecting timestamp, temperature, humidity
          System.err.println("Incorrect data format: " + value)
          null
        } else {
          try {
            val timestamp: String = parts(0)
            val temperature: Double = parts(1).toDouble
            val humidity: Double = parts(2).toDouble
            if (temperature > 38) {
              s"""{"timestamp": "$timestamp", "temperature": $temperature, "humidity": $humidity, "alert": "ALERT! High temperature"}"""
            } else {
              s"""{"timestamp": "$timestamp", "temperature": $temperature, "humidity": $humidity}"""
            }
          } catch {
            case e: NumberFormatException =>
              // Number parsing error handling
              System.err.println("Number parsing error: " + e.getMessage)
              null
          }
        }
      }
    })

    processedStream.to("processed-real-time-data")

    processedStream.foreach((key: String, value: String) => {
      if (value != null) {
        try {
          val writer = new PrintWriter(new FileWriter("D:\\kafka\\processed-data.json", true))
          writer.println(value)
          writer.close()
        } catch {
          case e: IOException =>
            System.err.println("Error while writing to file: " + e.getMessage)
        }
      }
    })

    val streams: KafkaStreams = new KafkaStreams(builder.build(), props)
    streams.start()
  }
}
