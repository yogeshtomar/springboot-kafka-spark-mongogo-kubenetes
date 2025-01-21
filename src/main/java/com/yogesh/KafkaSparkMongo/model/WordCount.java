package com.yogesh.KafkaSparkMongo.model;

import lombok.*;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class WordCount {
    @Id
    private String word;
    private Long count;
}
