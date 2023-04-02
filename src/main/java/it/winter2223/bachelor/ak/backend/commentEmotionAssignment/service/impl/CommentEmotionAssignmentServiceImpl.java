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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

import static it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationExceptionMessages.NO_USER_WITH_PASSED_ID;
import static it.winter2223.bachelor.ak.backend.comment.exception.CommentExceptionMessages.NO_COMMENT_WITH_ENTERED_ID;
import static it.winter2223.bachelor.ak.backend.commentEmotionAssignment.exception.CommentEmotionAssignmentExceptionMessages.*;

@Service
public class CommentEmotionAssignmentServiceImpl implements CommentEmotionAssignmentService {

    private final UserRepository userRepository;
    private final CommentEmotionAssignmentRepository assignmentRepository;
    private final CommentRepository commentRepository;
    private final CommentEmotionAssignmentMapper commentEmotionAssignmentMapper;
    private final Logger logger;

    CommentEmotionAssignmentServiceImpl(UserRepository userRepository,
                                        CommentEmotionAssignmentRepository assignmentRepository,
                                        CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.commentRepository = commentRepository;
        this.commentEmotionAssignmentMapper = new CommentEmotionAssignmentMapper();
        this.logger = LoggerFactory.getLogger(CommentEmotionAssignmentServiceImpl.class);
    }

    @Override
    @Transactional
    public List<CommentEmotionAssignmentOutput> postCommentEmotionAssignments(List<CommentEmotionAssignmentInput> assignmentInputs) {
        List<CommentEmotionAssignmentOutput> assignmentOutputs = new ArrayList<>();

        assignmentInputs.forEach(assignmentInput -> processAssignmentInput(assignmentOutputs, assignmentInput));

        return assignmentOutputs;
    }

    @Override
    public void generateCommentEmotionAssignmentsDataset(HttpServletResponse servletResponse) {
        configureResponse(servletResponse);

        Map<String, Emotion> commentToEmotionMap = getAssignmentsToExport();

        try (Writer writer = servletResponse.getWriter();
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

            csvPrinter.printRecord("comment", "emotion");
            for (Map.Entry<String, Emotion> entry : commentToEmotionMap.entrySet()) {
                csvPrinter.printRecord(entry.getKey(), entry.getValue());
            }

        } catch (IOException e) {
            throw new CommentEmotionAssignmentException(FAILED_TO_WRITE_CSV.getMessage(), e);
        }
    }

    private void processAssignmentInput(
            List<CommentEmotionAssignmentOutput> assignmentOutputs,
            CommentEmotionAssignmentInput assignmentInput) {
        validateUserId(assignmentInput.userId());
        Emotion emotion = getEnumFrom(assignmentInput.emotion());

        Comment comment = getComment(assignmentInput);
        checkIfAssignmentsExists(assignmentInput);

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
        if (!Emotion.contains(emotion)) {
            throw new CommentEmotionAssignmentException(WRONG_EMOTION.getMessage() + " (" + emotion + ")");
        }
        return Emotion.valueOf(emotion);
    }

    private Comment getComment(CommentEmotionAssignmentInput assignmentInput) {
        return commentRepository.findByCommentId(assignmentInput.commentId())
                .orElseThrow(() -> new CommentException(
                        NO_COMMENT_WITH_ENTERED_ID.getMessage() + " '" + assignmentInput.commentId() + "'"));
    }

    private void checkIfAssignmentsExists(CommentEmotionAssignmentInput assignmentInput) {
        if (assignmentRepository.findByUserIdAndCommentId(
                assignmentInput.userId(), assignmentInput.commentId()).isPresent()) {
            throw new CommentEmotionAssignmentException(
                    ASSIGNMENT_ALREADY_EXISTS.getMessage() + " (" + assignmentInput.commentId() + ")");
        }
    }

    private void configureResponse(HttpServletResponse servletResponse) {
        servletResponse.setContentType("text/csv");
        servletResponse.setCharacterEncoding("UTF-8");
        servletResponse.addHeader("Content-Disposition", "attachment; filename=\"assignments-dataset.csv\"");
    }

    private Map<String, Emotion> getAssignmentsToExport() {
        List<CommentEmotionAssignment> assignments = assignmentRepository.findByEmotionNotLike(Emotion.UNSPECIFIABLE);

        Map<String, List<CommentEmotionAssignment>> commentIdToAssigmnentMap = groupAssignmentsByCommentId(assignments);
        Map<String, List<Emotion>> commentIdToEmotionsListMap = mapToCommentIdToEmotionsListMap(commentIdToAssigmnentMap);
        Map<String, Emotion> commentIdToMostFrequentEmotionMap = mapToCommentIdToMostFrequentEmotionMap(commentIdToEmotionsListMap);

        return mapToCommentToEmotionMap(commentIdToMostFrequentEmotionMap);
    }

    private Map<String, List<CommentEmotionAssignment>> groupAssignmentsByCommentId(List<CommentEmotionAssignment> assignments) {
        return assignments.stream().collect(Collectors.groupingBy(CommentEmotionAssignment::getCommentId));
    }

    private Map<String, List<Emotion>> mapToCommentIdToEmotionsListMap(Map<String, List<CommentEmotionAssignment>> commentIdToAssigmnentMap) {
        Map<String, List<Emotion>> commentIdToEmotionsListMap = new HashMap<>();
        for (Map.Entry<String, List<CommentEmotionAssignment>> entry : commentIdToAssigmnentMap.entrySet()) {
            List<Emotion> emotions = entry.getValue().stream().map(CommentEmotionAssignment::getEmotion).toList();
            commentIdToEmotionsListMap.put(entry.getKey(), emotions);
        }
        return commentIdToEmotionsListMap;
    }

    private Map<String, Emotion> mapToCommentIdToMostFrequentEmotionMap(Map<String, List<Emotion>> commentIdToEmotionsListMap) {
        Map<String, Emotion> commentIdToMostFrequentEmotionMap = new HashMap<>();
        for (Map.Entry<String, List<Emotion>> entry : commentIdToEmotionsListMap.entrySet()) {
            commentIdToMostFrequentEmotionMap.put(entry.getKey(), getMostFrequentEmotion(entry.getValue()));
        }
        return commentIdToMostFrequentEmotionMap;
    }

    private Map<String, Emotion> mapToCommentToEmotionMap(Map<String, Emotion> commentIdToMostFrequentEmotionMap) {
        Map<String, Emotion> commentToEmotionMap = new HashMap<>();
        for (Map.Entry<String, Emotion> entry : commentIdToMostFrequentEmotionMap.entrySet()) {
            Optional<Comment> comment = commentRepository.findById(entry.getKey());
            comment.ifPresentOrElse(
                    c -> commentToEmotionMap.put(c.getContent(), entry.getValue()),
                    () -> logger.error("Comment with id: " + entry.getKey() + " not found in the database"));
        }
        return commentToEmotionMap;
    }

    private Emotion getMostFrequentEmotion(List<Emotion> emotions) {
        Map<Emotion, Integer> emotionToEmotionsNumberMap =
                new HashMap<>();

        emotions.forEach(emotion -> {
            if (emotionToEmotionsNumberMap.containsKey(emotion)) {
                int number = emotionToEmotionsNumberMap.get(emotion);
                number++;
                emotionToEmotionsNumberMap.put(emotion, number);
            } else {
                emotionToEmotionsNumberMap.put(emotion, 1);
            }
        });

        int maxCount = 0;
        Emotion emotion = null;

        for (Map.Entry<Emotion, Integer> entry : emotionToEmotionsNumberMap.entrySet()) {
            if (maxCount < entry.getValue()) {
                emotion = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        return emotion;
    }
}
