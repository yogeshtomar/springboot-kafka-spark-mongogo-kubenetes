package com.yogesh.KafkaSparkMongo.model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepository extends MongoRepository<WordCount, String> {
    @Query("{ 'word' : ?0 }")
    WordCount findByWord(String word);

    @Query(value = "{ 'word' : ?0 }", fields = "{ 'count' : 1 }")
    Long findCountByWord(String word);

    @Query(value = "{ 'word' : ?0 }", fields = "{ '_id' : 0, 'count' : 1 }")
    Long findCountByWordProjection(String word);
}
