package it.nlp.backend.textAnalysis.repository;

import it.nlp.backend.textAnalysis.model.Model;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ModelRepository extends MongoRepository<Model, String> {
}
