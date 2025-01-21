package com.yogesh.KafkaSparkMongo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Document(collation = "kafkaMessages")
public class MessageEntity {

    @Id
    private String id;
    private String data;
    private String timestamp;
}
