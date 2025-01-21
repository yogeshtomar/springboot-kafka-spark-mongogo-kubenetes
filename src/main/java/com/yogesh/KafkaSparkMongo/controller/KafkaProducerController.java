package com.yogesh.KafkaSparkMongo.controller;

import com.yogesh.KafkaSparkMongo.service.KafkaProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class KafkaProducerController {
    @Autowired
    private KafkaProducerService kafkaProducerService;

    private static final String TOPIC = "test-topic";
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerController.class);

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody String message) {
        logger.info("Message:{}", message);
        kafkaProducerService.sendMessage(TOPIC, message);
        return ResponseEntity.ok("Message sent to Kafka");
    }
}
