package it.winter2223.bachelor.ak.backend.emotionText.repository;

import it.winter2223.bachelor.ak.backend.emotionText.model.EmotionText;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmotionTextRepository extends MongoRepository<EmotionText, UUID> {

    boolean existsByOriginalSourceId(String originalSourceId);
}
