package it.winter2223.bachelor.ak.backend.commentEmotionAssignment.service.impl;

import it.winter2223.bachelor.ak.backend.comment.exception.CommentException;
import it.winter2223.bachelor.ak.backend.comment.persistence.Comment;
import it.winter2223.bachelor.ak.backend.comment.repository.CommentRepository;
import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.dto.CommentEmotionAssignmentInput;
import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.dto.CommentEmotionAssignmentOutput;
import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.exception.CommentEmotionAssignmentException;
import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.persistence.CommentEmotionAssignment;
import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.persistence.Emotion;
import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.repository.CommentEmotionAssignmentRepository;
import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.service.CommentEmotionAssignmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static it.winter2223.bachelor.ak.backend.comment.exception.CommentExceptionMessages.NO_COMMENT_WITH_ENTERED_ID;
import static it.winter2223.bachelor.ak.backend.commentEmotionAssignment.exception.CommentEmotionAssignmentExceptionMessages.ASSIGNMENT_ALREADY_EXISTS;
import static it.winter2223.bachelor.ak.backend.commentEmotionAssignment.exception.CommentEmotionAssignmentExceptionMessages.WRONG_EMOTION;

@Service
public class CommentEmotionAssignmentServiceImpl implements CommentEmotionAssignmentService {

    private final CommentEmotionAssignmentRepository assignmentRepository;
    private final CommentRepository commentRepository;
    private final CommentEmotionAssignmentMapper commentEmotionAssignmentMapper;

    CommentEmotionAssignmentServiceImpl(CommentEmotionAssignmentRepository assignmentRepository,
                                        CommentRepository commentRepository) {
        this.assignmentRepository = assignmentRepository;
        this.commentRepository = commentRepository;
        this.commentEmotionAssignmentMapper = new CommentEmotionAssignmentMapper();
    }

    @Override
    @Transactional
    public CommentEmotionAssignmentOutput postCommentEmotionAssignment(CommentEmotionAssignmentInput assignmentInput) {
        validateCommentId(assignmentInput);
        Emotion emotion = getEnumFrom(assignmentInput.emotion());

        Comment comment = commentRepository.findByCommentId(assignmentInput.commentId())
                .orElseThrow(() -> new CommentException(NO_COMMENT_WITH_ENTERED_ID.getMessage()));
        if (assignmentRepository.findByUserIdAndCommentId(
                assignmentInput.userId(), assignmentInput.commentId()).isPresent()) {
            throw new CommentEmotionAssignmentException(ASSIGNMENT_ALREADY_EXISTS.getMessage());
        }

        comment.increaseAssignmentsNumber();
        commentRepository.save(comment);

        CommentEmotionAssignment commentEmotionAssignment = CommentEmotionAssignment.builder()
                .commentEmotionAssignmentId(UUID.randomUUID())
                .userId(assignmentInput.userId())
                .commentId(assignmentInput.commentId())
                .emotion(emotion)
                .build();

        return commentEmotionAssignmentMapper.mapToCommentEmotionAssignmentOutput(assignmentRepository.save(commentEmotionAssignment));
    }

    private void validateCommentId(CommentEmotionAssignmentInput assignmentInput) {
        if(!commentRepository.existsById(assignmentInput.commentId())) {
            throw new CommentException(NO_COMMENT_WITH_ENTERED_ID.getMessage());
        }
    }

    private Emotion getEnumFrom(String emotion) {
        if(!Emotion.contains(emotion)) {
            throw new CommentEmotionAssignmentException(WRONG_EMOTION.getMessage());
        }
        return Emotion.valueOf(emotion);
    }
}
