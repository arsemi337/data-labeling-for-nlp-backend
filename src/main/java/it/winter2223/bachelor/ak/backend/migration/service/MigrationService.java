package it.winter2223.bachelor.ak.backend.migration.service;

import it.winter2223.bachelor.ak.backend.comment.repository.CommentRepository;
import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.repository.CommentEmotionAssignmentRepository;
import org.springframework.stereotype.Service;

@Service
public class MigrationService {
    private final CommentRepository commentRepository;
    private final CommentEmotionAssignmentRepository assignmentRepository;

    public MigrationService(CommentRepository commentRepository, CommentEmotionAssignmentRepository assignmentRepository) {
        this.commentRepository = commentRepository;
        this.assignmentRepository = assignmentRepository;
    }
}
