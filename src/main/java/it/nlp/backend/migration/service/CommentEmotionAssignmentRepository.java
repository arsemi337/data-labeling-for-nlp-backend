package it.nlp.backend.migration.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentEmotionAssignmentRepository extends MongoRepository<CommentEmotionAssignment, UUID> {

    List<CommentEmotionAssignment> findByUserId(String userId);
    Optional<CommentEmotionAssignment> findByUserIdAndCommentId(String userId, String commentId);
    List<CommentEmotionAssignment> findByEmotionNotLike(Emotion emotion);
}
