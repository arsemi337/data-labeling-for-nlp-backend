package it.winter2223.bachelor.ak.backend.comment.repository;

import it.winter2223.bachelor.ak.backend.comment.persistence.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

    Optional<Comment> findByCommentId(String commentId);
}
