package it.nlp.backend.migration.service;

import it.nlp.backend.emotionText.model.Emotion;
import it.nlp.backend.emotionText.model.EmotionText;
import it.nlp.backend.emotionText.repository.EmotionTextRepository;
import it.nlp.backend.utils.TimeSupplier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static it.nlp.backend.emotionText.model.TextSource.YOUTUBE;

@Service
public class MigrationService {
    private final CommentRepository commentRepository;
    private final EmotionTextRepository textRepository;
    private final CommentEmotionAssignmentRepository assignmentRepository;
    private final TimeSupplier timeSupplier;

    public MigrationService(CommentRepository commentRepository,
                            EmotionTextRepository textRepository,
                            CommentEmotionAssignmentRepository assignmentRepository,
                            TimeSupplier timeSupplier) {
        this.commentRepository = commentRepository;
        this.textRepository = textRepository;
        this.assignmentRepository = assignmentRepository;
        this.timeSupplier = timeSupplier;
    }

    public void migrateComments() {
        Map<String, List<CommentEmotionAssignment>> commentIdToCommentsListMap = assignmentRepository.findAll().stream()
                .collect(Collectors.groupingBy(CommentEmotionAssignment::getCommentId));

        commentRepository.findAll().stream()
                .map(comment -> EmotionText.builder()
                        .emotionTextId(UUID.randomUUID())
                        .createdAt(timeSupplier.get())
                        .content(comment.getContent())
                        .originalSourceId(comment.getCommentId())
                        .textSource(YOUTUBE)
                        .assignedEmotions(mapToEmotions(comment, commentIdToCommentsListMap))
                        .build())
                .forEach(textRepository::save);
    }

    private List<Emotion> mapToEmotions(Comment comment, Map<String, List<CommentEmotionAssignment>> map) {
        if (map.containsKey(comment.getCommentId())) {
            return map.get(comment.getCommentId()).stream()
                    .map(assignment -> Emotion.valueOf(assignment.getEmotion().toString()))
                    .toList();
        } else {
            return new ArrayList<>();
        }
    }

    public void checkAssignmentsNumber() {
        long number = textRepository.findAll().stream()
                .filter(emotionText -> !emotionText.getAssignedEmotions().isEmpty())
                .count();
        System.out.println(number);
    }
}
