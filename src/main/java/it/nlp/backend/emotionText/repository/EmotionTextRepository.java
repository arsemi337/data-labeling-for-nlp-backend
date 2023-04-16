package it.nlp.backend.emotionText.repository;

import it.nlp.backend.emotionText.model.EmotionText;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmotionTextRepository extends MongoRepository<EmotionText, UUID> {

    boolean existsByOriginalSourceId(String originalSourceId);
}
