package it.winter2223.bachelor.ak.backend.comments.repository;

import it.winter2223.bachelor.ak.backend.comments.persistence.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentsRepository extends MongoRepository<Comment, Long> {
//    Optional<Model> findByName(String name);

    Page<Comment> findByIsAssigned(boolean isAssigned, Pageable pageable);
}
