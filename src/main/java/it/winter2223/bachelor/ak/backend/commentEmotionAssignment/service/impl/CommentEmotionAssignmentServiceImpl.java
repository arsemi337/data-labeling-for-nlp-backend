package it.winter2223.bachelor.ak.backend.commentEmotionAssignment.service.impl;

import it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationException;
import it.winter2223.bachelor.ak.backend.authentication.repository.UserRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationExceptionMessages.NO_USER_WITH_PASSED_ID;
import static it.winter2223.bachelor.ak.backend.comment.exception.CommentExceptionMessages.NO_COMMENT_WITH_ENTERED_ID;
import static it.winter2223.bachelor.ak.backend.commentEmotionAssignment.exception.CommentEmotionAssignmentExceptionMessages.ASSIGNMENT_ALREADY_EXISTS;
import static it.winter2223.bachelor.ak.backend.commentEmotionAssignment.exception.CommentEmotionAssignmentExceptionMessages.WRONG_EMOTION;

@Service
public class CommentEmotionAssignmentServiceImpl implements CommentEmotionAssignmentService {

    private final UserRepository userRepository;
    private final CommentEmotionAssignmentRepository assignmentRepository;
    private final CommentRepository commentRepository;
    private final CommentEmotionAssignmentMapper commentEmotionAssignmentMapper;

    CommentEmotionAssignmentServiceImpl(UserRepository userRepository,
                                        CommentEmotionAssignmentRepository assignmentRepository,
                                        CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.commentRepository = commentRepository;
        this.commentEmotionAssignmentMapper = new CommentEmotionAssignmentMapper();
    }

    @Override
    @Transactional
    public List<CommentEmotionAssignmentOutput> postCommentEmotionAssignment(List<CommentEmotionAssignmentInput> assignmentInputs) {
        List<CommentEmotionAssignmentOutput> assignmentOutputs = new ArrayList<>();

        assignmentInputs.forEach(assignmentInput -> processAssignmentInput(assignmentOutputs, assignmentInput));

        return assignmentOutputs;
    }

    private void processAssignmentInput(
            List<CommentEmotionAssignmentOutput> assignmentOutputs,
            CommentEmotionAssignmentInput assignmentInput) {
        validateUserId(assignmentInput.userId());
        Emotion emotion = getEnumFrom(assignmentInput.emotion());

        Comment comment = commentRepository.findByCommentId(assignmentInput.commentId())
                .orElseThrow(() -> new CommentException(
                        NO_COMMENT_WITH_ENTERED_ID.getMessage() + " '" + assignmentInput.commentId() + "'"));
        if (assignmentRepository.findByUserIdAndCommentId(
                assignmentInput.userId(), assignmentInput.commentId()).isPresent()) {
            throw new CommentEmotionAssignmentException(
                    ASSIGNMENT_ALREADY_EXISTS.getMessage() + " (" + assignmentInput.commentId() + ")");
        }

        comment.increaseAssignmentsNumber();
        commentRepository.save(comment);

        CommentEmotionAssignment commentEmotionAssignment = CommentEmotionAssignment.builder()
                .commentEmotionAssignmentId(UUID.randomUUID())
                .userId(assignmentInput.userId())
                .commentId(assignmentInput.commentId())
                .emotion(emotion)
                .build();

        assignmentOutputs.add(
                commentEmotionAssignmentMapper
                        .mapToCommentEmotionAssignmentOutput(
                                assignmentRepository.save(commentEmotionAssignment)));
    }

    private void validateUserId(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new FirebaseAuthenticationException(NO_USER_WITH_PASSED_ID.getMessage() + " '" + userId + "'");
        }
    }

    private Emotion getEnumFrom(String emotion) {
        if(!Emotion.contains(emotion)) {
            throw new CommentEmotionAssignmentException(WRONG_EMOTION.getMessage() + " (" + emotion + ")");
        }
        return Emotion.valueOf(emotion);
    }
}
