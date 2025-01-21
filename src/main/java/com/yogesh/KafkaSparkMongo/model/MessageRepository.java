package com.yogesh.KafkaSparkMongo.model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<MessageEntity, String> {

}
