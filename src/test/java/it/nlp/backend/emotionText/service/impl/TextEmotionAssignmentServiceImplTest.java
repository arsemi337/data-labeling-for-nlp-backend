package it.nlp.backend.emotionText.service.impl;

import it.nlp.backend.authentication.model.User;
import it.nlp.backend.authentication.repository.UserRepository;
import it.nlp.backend.emotionText.dto.TextEmotionAssignmentInput;
import it.nlp.backend.emotionText.dto.TextEmotionAssignmentOutput;
import it.nlp.backend.emotionText.model.Emotion;
import it.nlp.backend.emotionText.model.EmotionText;
import it.nlp.backend.emotionText.repository.EmotionTextRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static it.nlp.backend.exception.messages.SecurityExceptionMessages.NO_USER_WITH_PASSED_EMAIL;
import static it.nlp.backend.exception.messages.TextEmotionAssignmentExceptionMessages.ASSIGNMENT_ALREADY_EXISTS;
import static it.nlp.backend.exception.messages.TextEmotionAssignmentExceptionMessages.WRONG_EMOTION;
import static it.nlp.backend.exception.messages.TextExceptionMessages.NO_TEXT_WITH_ENTERED_ID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TextEmotionAssignmentServiceImplTest {

    @InjectMocks
    TextEmotionAssignmentServiceImpl underTest;
    @Mock
    UserRepository userRepository;
    @Mock
    EmotionTextRepository emotionTextRepository;

    @ParameterizedTest
    @EnumSource(Emotion.class)
    @DisplayName("when proper assignment input is passed, a text emotion assignment should be created")
    void shouldPostEmotionAssignment(Emotion emotion) {
        String userEmail = "userEmail";
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .userId(userId)
                .assignedEmotionTextIds(new ArrayList<>())
                .build();
        UUID emotionTextId = UUID.randomUUID();
        List<TextEmotionAssignmentInput> input = List.of(TextEmotionAssignmentInput.builder()
                .textId(emotionTextId.toString())
                .emotion(emotion.toString())
                .build());
        EmotionText text = EmotionText.builder()
                .emotionTextId(emotionTextId)
                .assignedEmotions(new ArrayList<>())
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(emotionTextRepository.findById(any(UUID.class))).thenReturn(Optional.of(text));
        when(emotionTextRepository.save(any(EmotionText.class))).thenAnswer(answer -> answer.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(answer -> answer.getArgument(0));

        List<TextEmotionAssignmentOutput> output = underTest.postTextEmotionAssignments(userEmail, input);

        assertEquals(userId, output.get(0).userId());
        assertEquals(emotionTextId, output.get(0).textId());
        assertEquals(emotion.toString(), output.get(0).emotionDto().toString());
        verify(userRepository).findByEmail(userEmail);
        verify(emotionTextRepository).save(any(EmotionText.class));
        verify(userRepository).save(any(User.class));
    }

    @ParameterizedTest
    @EnumSource(Emotion.class)
    @DisplayName("when invalid user email is passed, a NoSuchElementException exception should be thrown")
    void shouldThrowNoSuchElementExceptionWhenPostingAssignmentForWrongUserEmail(Emotion emotion) {
        String userEmail = "userEmail";
        UUID emotionTextId = UUID.randomUUID();
        List<TextEmotionAssignmentInput> input = List.of(TextEmotionAssignmentInput.builder()
                .textId(emotionTextId.toString())
                .emotion(emotion.toString())
                .build());

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> underTest.postTextEmotionAssignments(userEmail, input))
                .withMessage(NO_USER_WITH_PASSED_EMAIL.getMessage() + userEmail);
    }

    @ParameterizedTest
    @ValueSource(strings = {"joy", "enjoyment", "peace"})
    @DisplayName("when invalid emotion is passed, a IllegalArgumentException exception should be thrown")
    void shouldThrowIllegalArgumentExceptionWhenPostingAssignmentForWrongEmotion(String emotion) {
        String userEmail = "userEmail";
        UUID emotionTextId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        List<TextEmotionAssignmentInput> input = List.of(TextEmotionAssignmentInput.builder()
                .textId(emotionTextId.toString())
                .emotion(emotion)
                .build());
        User user = User.builder()
                .userId(userId)
                .assignedEmotionTextIds(new ArrayList<>())
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> underTest.postTextEmotionAssignments(userEmail, input))
                .withMessage(WRONG_EMOTION.getMessage() + input.get(0).emotion());
    }

    @ParameterizedTest
    @EnumSource(Emotion.class)
    @DisplayName("when invalid text id is passed, a IllegalArgumentException exception should be thrown")
    void shouldThrowIllegalArgumentExceptionWhenPostingAssignmentForWrongTextId(Emotion emotion) {
        String userEmail = "userEmail";
        UUID emotionTextId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        List<TextEmotionAssignmentInput> input = List.of(TextEmotionAssignmentInput.builder()
                .textId(emotionTextId.toString())
                .emotion(emotion.toString())
                .build());
        User user = User.builder()
                .userId(userId)
                .assignedEmotionTextIds(new ArrayList<>())
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(emotionTextRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> underTest.postTextEmotionAssignments(userEmail, input))
                .withMessage(NO_TEXT_WITH_ENTERED_ID.getMessage() + input.get(0).textId());
    }

    @ParameterizedTest
    @EnumSource(Emotion.class)
    @DisplayName("when already existing assignment is passe, a IllegalArgumentException exception should be thrown")
    void shouldThrowIllegalArgumentExceptionWhenTryingToDuplicateAssignment(Emotion emotion) {
        String userEmail = "userEmail";
        UUID emotionTextId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        List<TextEmotionAssignmentInput> input = List.of(TextEmotionAssignmentInput.builder()
                .textId(emotionTextId.toString())
                .emotion(emotion.toString())
                .build());
        EmotionText text = EmotionText.builder()
                .emotionTextId(emotionTextId)
                .assignedEmotions(new ArrayList<>())
                .build();
        User user = User.builder()
                .userId(userId)
                .assignedEmotionTextIds(List.of(text.getEmotionTextId()))
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(emotionTextRepository.findById(any(UUID.class))).thenReturn(Optional.of(text));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> underTest.postTextEmotionAssignments(userEmail, input))
                .withMessage(ASSIGNMENT_ALREADY_EXISTS.getMessage() + input.get(0).textId());
    }
}
