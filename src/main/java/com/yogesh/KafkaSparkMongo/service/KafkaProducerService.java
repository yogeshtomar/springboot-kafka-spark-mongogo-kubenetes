package com.yogesh.KafkaSparkMongo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
        logger.info("Sent Message: {}", message);
    }
}
