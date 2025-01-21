package com.yogesh.KafkaSparkMongo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yogesh.KafkaSparkMongo.config.KafkaConsumerConfig;
import com.yogesh.KafkaSparkMongo.model.MessageEntity;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class KafkaConsumerService {

    @Autowired
    private KafkaConsumerConfig kafkaConsumerConfig;
    @Autowired
    private MongoService mongoService;

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void startSparkStream() {
        logger.info("Starting Kafka Consumer Service with Apache Spark Service");
        SparkConf sparkConf = new SparkConf()
                .setAppName("KafkaSparkMongodbApp")
                .setMaster("local[*]");

            try {
                JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(5));
                Collection<String> topics = Collections.singletonList("test-topic");
                JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(
                        jssc,
                        LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.Subscribe(topics, kafkaConsumerConfig.getKafkaConsumerParams()));

                stream.foreachRDD(rdd -> {

                    rdd.foreach(message -> {
                        // Process the message (e.g., save to MongoDB)
                        logger.info("Received message: {}", message.value());
                    });
                });

                stream.foreachRDD(this::processAndSaveMessages);

                jssc.start();
                try {
                    jssc.awaitTermination();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
    }


    private void processAndSaveMessages(JavaRDD<ConsumerRecord<String, String>> rdd) {
        // Extract only the serializable fields (e.g., message value)
        JavaRDD<String> messages = rdd.map(ConsumerRecord::value);

        // Collect messages to the driver and process
        List<MessageEntity> processedMessages = messages.collect().stream()
                .map(this::convertToMessage)
                .filter(msg -> msg != null)
                .collect(Collectors.toList());

        if (!processedMessages.isEmpty()) {
            mongoService.saveMessages(processedMessages);
            logger.info("Saved {} messages to MongoDB", processedMessages.size());
        }

        List<String> sentences = messages.collect().stream()
                .map(this::getData)
                .filter(msg -> msg != null)
                .collect(Collectors.toList());

        Map<String, Long> wordFrequency = sentences.stream()
                .flatMap(sentence -> Arrays.stream(sentence.split("\\s+"))) // Split each sentence into words
                .collect(Collectors.groupingBy(word -> word, Collectors.counting()));

        wordFrequency.forEach((word, frequency) -> {
            mongoService.saveOrUpdateWordCount(word, frequency);
            logger.info("Word : {}, count : {}", word, wordFrequency);
        });

    }

    private String getData(String jsonMessage) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonMessage);
            String data = jsonNode.get("data").asText();
            return data;
        } catch (Exception e) {
            logger.error("Failed to parse message to word count: {}", jsonMessage);
            e.printStackTrace();
            return null;
        }
    }

    private MessageEntity convertToMessage(String jsonMessage) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonMessage);
            String data = jsonNode.get("data").asText();
            MessageEntity msg = new MessageEntity();
            msg.setData(data);
            return msg;
        } catch (Exception e) {
            logger.error("Failed to parse message: {}", jsonMessage);
            e.printStackTrace();
            return null;
        }
    }
}
