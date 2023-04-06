package it.winter2223.bachelor.ak.backend.emotionText.service.impl;

import it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationException;
import it.winter2223.bachelor.ak.backend.authentication.model.User;
import it.winter2223.bachelor.ak.backend.authentication.repository.UserRepository;
import it.winter2223.bachelor.ak.backend.comment.exception.CommentException;
import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.exception.CommentEmotionAssignmentException;
import it.winter2223.bachelor.ak.backend.emotionText.dto.TextEmotionAssignmentInput;
import it.winter2223.bachelor.ak.backend.emotionText.dto.TextEmotionAssignmentOutput;
import it.winter2223.bachelor.ak.backend.emotionText.model.Emotion;
import it.winter2223.bachelor.ak.backend.emotionText.model.EmotionText;
import it.winter2223.bachelor.ak.backend.emotionText.repository.EmotionTextRepository;
import it.winter2223.bachelor.ak.backend.emotionText.service.TextEmotionAssignmentService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationExceptionMessages.NO_USER_WITH_PASSED_ID;
import static it.winter2223.bachelor.ak.backend.comment.exception.CommentExceptionMessages.NO_COMMENT_WITH_ENTERED_ID;
import static it.winter2223.bachelor.ak.backend.commentEmotionAssignment.exception.CommentEmotionAssignmentExceptionMessages.*;

@Service
public class TextEmotionAssignmentServiceImpl implements TextEmotionAssignmentService {

    private final UserRepository userRepository;
    private final EmotionTextRepository textRepository;
    private final TextEmotionAssignmentMapper textEmotionAssignmentMapper;

    TextEmotionAssignmentServiceImpl(UserRepository userRepository,
                                     EmotionTextRepository textRepository) {
        this.userRepository = userRepository;
        this.textRepository = textRepository;
        this.textEmotionAssignmentMapper = new TextEmotionAssignmentMapper();
    }

    @Override
    @Transactional
    public List<TextEmotionAssignmentOutput> postTextEmotionAssignments(List<TextEmotionAssignmentInput> assignmentInputs) {
        List<TextEmotionAssignmentOutput> assignmentOutputs = new ArrayList<>();

        assignmentInputs.forEach(assignmentInput -> processAssignmentInput(assignmentOutputs, assignmentInput));

        return assignmentOutputs;
    }

    @Override
    public void generateTextEmotionAssignmentsDataset(HttpServletResponse servletResponse) {
        configureResponse(servletResponse);

        Map<String, Emotion> textToEmotionMap = getAssignmentsToExport();

        try (Writer writer = servletResponse.getWriter();
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

            csvPrinter.printRecord("emotionTextContent", "emotion");
            for (Map.Entry<String, Emotion> entry : textToEmotionMap.entrySet()) {
                csvPrinter.printRecord(entry.getKey(), entry.getValue());
            }

        } catch (IOException e) {
            throw new CommentEmotionAssignmentException(FAILED_TO_WRITE_CSV.getMessage(), e);
        }
    }

    private void processAssignmentInput(
            List<TextEmotionAssignmentOutput> assignmentOutputs,
            TextEmotionAssignmentInput assignmentInput) {
        UUID userId = UUID.fromString(assignmentInput.userId());
        validateUserId(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(); //TODO: zrobić wyjątki
        Emotion emotion = getEnumFrom(assignmentInput.emotion());

        EmotionText emotionText = getEmotionText(assignmentInput);

        checkIfAssignmentExists(user.getAssignedEmotionTextIds(), emotionText.getEmotionTextId());
        user.getAssignedEmotionTextIds().add(emotionText.getEmotionTextId());
        emotionText.getAssignedEmotions().add(emotion);

        textRepository.save(emotionText);
        userRepository.save(user);

        assignmentOutputs.add(
                textEmotionAssignmentMapper.mapToTextEmotionAssignmentOutput(
                        userId, emotionText.getEmotionTextId(), emotion));
    }

    private void validateUserId(UUID userId) {
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

    private EmotionText getEmotionText(TextEmotionAssignmentInput assignmentInput) {
        UUID textId = UUID.fromString(assignmentInput.userId());
        return textRepository.findById(textId)
                .orElseThrow(() -> new CommentException(
                        NO_COMMENT_WITH_ENTERED_ID.getMessage() + " '" + textId + "'"));
    }

    private void checkIfAssignmentExists(List<UUID> userAssignedEmotionTexts, UUID emotionTextId) {
        if (userAssignedEmotionTexts.contains(emotionTextId)) {
            throw new CommentEmotionAssignmentException(
                    ASSIGNMENT_ALREADY_EXISTS.getMessage() + " (" + emotionTextId + ")");
        }
    }

    private void configureResponse(HttpServletResponse servletResponse) {
        servletResponse.setContentType("text/csv");
        servletResponse.setCharacterEncoding("UTF-8");
        servletResponse.addHeader("Content-Disposition", "attachment; filename=\"assignments-dataset.csv\"");
    }

    private Map<String, Emotion> getAssignmentsToExport() {
        List<EmotionText> emotionTextsWithValidAssignments = textRepository.findAll().stream()
                .map(emotionText -> {
                    var filteredAssignedEmotions = emotionText.getAssignedEmotions().stream()
                            .filter(emotion -> !emotion.equals(Emotion.UNSPECIFIABLE)).toList();

                    return EmotionText.builder()
                            .emotionTextId(emotionText.getEmotionTextId())
                            .createdAt(emotionText.getCreatedAt())
                            .content(emotionText.getContent())
                            .originalSourceId(emotionText.getOriginalSourceId())
                            .assignedEmotions(filteredAssignedEmotions)
                            .build();
                })
                .filter(emotionText -> !emotionText.getAssignedEmotions().isEmpty())
                .toList();

        return mapToTextContentToMostFrequentEmotionMap(emotionTextsWithValidAssignments);
    }

    private Map<String, Emotion> mapToTextContentToMostFrequentEmotionMap(List<EmotionText> emotionTexts) {
        Map<String, Emotion> textContentToMostFrequentEmotionMap = new HashMap<>();

        emotionTexts.forEach(emotionText ->
                textContentToMostFrequentEmotionMap.put(
                        emotionText.getContent(),
                        getMostFrequentEmotion(emotionText.getAssignedEmotions()))
        );
        return textContentToMostFrequentEmotionMap;
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
