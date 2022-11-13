package it.winter2223.bachelor.ak.backend.commentEmotionAssignment.repository;

import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.persistence.CommentEmotionAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentEmotionAssignmentRepository extends MongoRepository<CommentEmotionAssignment, UUID> {

    List<CommentEmotionAssignment> findByUserId(String userId);
    List<CommentEmotionAssignment> findByCommentId(String commentId);
}
