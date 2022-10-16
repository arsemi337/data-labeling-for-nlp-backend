package it.winter2223.bachelor.ak.backend.comments.repository;

import it.winter2223.bachelor.ak.backend.comments.persistence.Model;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CommentsRepository extends MongoRepository<Model, Long> {
    Optional<Model> findByName(String name);
}
