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
    public void generateCommentEmotionAssignmentsDataset(HttpServletResponse servletResponse) {
        servletResponse.setContentType("text/csv");
        servletResponse.setCharacterEncoding("UTF-8");
        servletResponse.addHeader("Content-Disposition","attachment; filename=\"assignments-dataset.csv\"");

        Writer writer;
        try {
            writer = servletResponse.getWriter();
        } catch (IOException e) {
            throw new CommentEmotionAssignmentException(FAILED_TO_OBTAIN_STREAM_WRITER.getMessage());
        }

        List<CommentEmotionAssignment> assignments = assignmentRepository.findByEmotionNotLike(Emotion.UNSPECIFIABLE);

        Map<String, List<CommentEmotionAssignment>> assignmentsListsGrouped =
                assignments.stream().collect(Collectors.groupingBy(CommentEmotionAssignment::getCommentId));

        Map<String, List<Emotion>> assignmentsGroupedByCommentId = new HashMap<>();
        for (Map.Entry<String, List<CommentEmotionAssignment>> entry : assignmentsListsGrouped.entrySet()) {
            List<Emotion> emotions = entry.getValue().stream().map(CommentEmotionAssignment::getEmotion).toList();
            assignmentsGroupedByCommentId.put(entry.getKey(), emotions);
        }

        Map<String, String> mostPopularAssignmentsMap = new HashMap<>();
        for (Map.Entry<String, List<Emotion>> entry : assignmentsGroupedByCommentId.entrySet()) {
            mostPopularAssignmentsMap.put(entry.getKey(), mostFrequentEmotion(entry.getValue()));
        }

        Map<String, String> assignmentsToExport = new HashMap<>();
        for (Map.Entry<String, String> entry : mostPopularAssignmentsMap.entrySet()) {
            Optional<Comment> comment = commentRepository.findById(entry.getKey());
            comment.ifPresent(c -> assignmentsToExport.put(c.getContent(), entry.getValue()));
            if (comment.isEmpty()) {
                System.out.println(entry.getKey());
            }
        }

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            csvPrinter.printRecord("comment", "emotion");
            for (Map.Entry<String, String> entry : assignmentsToExport.entrySet()) {
                csvPrinter.printRecord(entry.getKey(), entry.getValue());
            }
        } catch (IOException e) {
            throw new CommentEmotionAssignmentException(FAILED_TO_WRITE_CSV.getMessage() + e);
        }
    }

    private String mostFrequentEmotion(List<Emotion> emotions) {
        Map<String, Integer> hp =
                new HashMap<>();

        emotions.forEach(emotion -> {
            String key = emotion.toString();
            if (hp.containsKey(key)) {
                int freq = hp.get(key);
                freq++;
                hp.put(key, freq);
            } else {
                hp.put(key, 1);
            }
        });

        int maxCount = 0;
        String emotion = "";

        for (Map.Entry<String, Integer> entry : hp.entrySet()) {
            if (maxCount < entry.getValue()) {
                emotion = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        return emotion;
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
