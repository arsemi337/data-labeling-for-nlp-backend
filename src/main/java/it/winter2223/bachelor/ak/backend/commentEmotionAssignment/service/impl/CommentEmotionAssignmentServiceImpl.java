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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

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

    @Override
    public void getCommentEmotionAssignments(HttpServletResponse servletResponse) {
        servletResponse.setContentType("text/csv");
        servletResponse.setCharacterEncoding("UTF-8");
        servletResponse.addHeader("Content-Disposition","attachment; filename=\"assignments.csv\"");

        Writer writer;
        try {
            writer = servletResponse.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, String> thingsToCSV = new HashMap<>();
//        List<CommentEmotionAssignment> assignments = assignmentRepository.findAll();
//        assignments.forEach(assignment -> {
//            Optional<Comment> comment = commentRepository.findById(assignment.getCommentId());
//            comment.ifPresent(s -> thingsToCSV.put(s.getContent(), assignment.getEmotion().toString()));
//            if (comment.isEmpty()) {
//                System.out.println(assignment.getCommentId());
//            }
//        });

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
//            for (Map.Entry<String, String> entry : thingsToCSV.entrySet()) {
//                csvPrinter.printRecord(entry.getKey(), entry.getValue());
//            }
            csvPrinter.printRecord("ąęćźżóśłńcoto", "ąężćódobrasprawa");
        } catch (IOException e) {
            System.out.println("Error while writing csv " + e);
        }
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
