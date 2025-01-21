package com.yogesh.KafkaSparkMongo.service;

import com.yogesh.KafkaSparkMongo.model.MessageEntity;
import com.yogesh.KafkaSparkMongo.model.MessageRepository;
import com.yogesh.KafkaSparkMongo.model.WordCount;
import com.yogesh.KafkaSparkMongo.model.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class MongoService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private WordRepository wordRepository;

    public void saveMessages(List<MessageEntity> messages) {
        for (MessageEntity msg : messages) {
            msg.setId(String.valueOf(UUID.randomUUID()));
            msg.setTimestamp(new Date().toString());
        }
        messageRepository.saveAll(messages);
    }

    public void saveOrUpdateWordCount(String word, Long frequency) {
        WordCount wordCount = wordRepository.findByWord(word);

        if (wordCount == null) {
            wordCount = new WordCount(word, 1L);
        } else {
            wordCount.setCount(wordCount.getCount() + frequency);
        }

        wordRepository.save(wordCount);
    }
}
