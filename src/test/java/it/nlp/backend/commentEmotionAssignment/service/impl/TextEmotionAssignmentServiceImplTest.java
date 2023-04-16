//package it.winter2223.bachelor.ak.backend.commentEmotionAssignment.service.impl;
//
//import it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationException;
//import it.winter2223.bachelor.ak.backend.authentication.repository.UserRepository;
//import it.winter2223.bachelor.ak.backend.comment.exception.CommentException;
//import it.winter2223.bachelor.ak.backend.comment.model.Comment;
//import it.winter2223.bachelor.ak.backend.comment.repository.CommentRepository;
//import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.dto.CommentEmotionAssignmentInput;
//import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.dto.CommentEmotionAssignmentOutput;
//import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.exception.CommentEmotionAssignmentException;
//import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.model.CommentEmotionAssignment;
//import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.model.Emotion;
//import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.repository.CommentEmotionAssignmentRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.EnumSource;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//
//import static it.winter2223.bachelor.ak.backend.authentication.exception.FirebaseAuthenticationExceptionMessages.NO_USER_WITH_PASSED_ID;
//import static it.winter2223.bachelor.ak.backend.comment.exception.CommentExceptionMessages.NO_COMMENT_WITH_ENTERED_ID;
//import static it.winter2223.bachelor.ak.backend.commentEmotionAssignment.exception.CommentEmotionAssignmentExceptionMessages.ASSIGNMENT_ALREADY_EXISTS;
//import static it.winter2223.bachelor.ak.backend.commentEmotionAssignment.exception.CommentEmotionAssignmentExceptionMessages.WRONG_EMOTION;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class TextEmotionAssignmentServiceImplTest {
//
//    @InjectMocks
//    CommentEmotionAssignmentServiceImpl underTest;
//
//    @Mock
//    UserRepository userRepository;
//
//    @Mock
//    CommentEmotionAssignmentRepository assignmentRepository;
//
//    @Mock
//    CommentRepository commentRepository;
//
//    @ParameterizedTest
//    @EnumSource(Emotion.class)
//    @DisplayName("when proper assignment input is passed, a comment emotion assignment should be created")
//    void shouldPostEmotionAssignment(Emotion emotion) {
//        List<CommentEmotionAssignmentInput> input = List.of(CommentEmotionAssignmentInput.builder()
//                .userId("userId")
//                .commentId("emotionTextId")
//                .emotion(emotion.toString())
//                .build());
//        Comment comment = Comment.builder()
//                        .commentId("emotionTextId")
//                        .content("content")
//                        .assignmentsNumber(0)
//                        .build();
//
//        when(userRepository.existsById(anyString())).thenReturn(true);
//        when(commentRepository.findByCommentId(anyString())).thenReturn(Optional.of(comment));
//        when(assignmentRepository.findByUserIdAndCommentId(anyString(), anyString())).thenReturn(Optional.empty());
//        when(commentRepository.save(any(Comment.class))).thenAnswer(answer -> answer.getArgument(0));
//        when(assignmentRepository.save(any(CommentEmotionAssignment.class))).thenAnswer(answer -> answer.getArgument(0));
//
//        List<CommentEmotionAssignmentOutput> output = underTest.postCommentEmotionAssignments(input);
//
//        assertEquals("userId", output.get(0).userId());
//        assertEquals("emotionTextId", output.get(0).commentId());
//        assertEquals(emotion.toString(), output.get(0).emotionDto().toString());
//        verify(commentRepository).save(any());
//        verify(assignmentRepository).save(any());
//    }
//
//    @ParameterizedTest
//    @EnumSource(Emotion.class)
//    @DisplayName("when invalid user id is passed, a FirebaseAuthenticationException exception should be thrown")
//    void shouldThrowFirebaseAuthenticationExceptionWhenPostingAssignmentForWrongUserId(Emotion emotion) {
//        List<CommentEmotionAssignmentInput> input = List.of(CommentEmotionAssignmentInput.builder()
//                .userId("userId")
//                .commentId("emotionTextId")
//                .emotion(emotion.toString())
//                .build());
//
//        when(userRepository.existsById(anyString())).thenReturn(false);
//
//        assertThatExceptionOfType(FirebaseAuthenticationException.class)
//                .isThrownBy(() -> underTest.postCommentEmotionAssignments(input))
//                .withMessage(NO_USER_WITH_PASSED_ID.getMessage() + " '" + input.get(0).userId() + "'");
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings = {"joy", "enjoyment", "peace"})
//    @DisplayName("when invalid emotion is passed, a CommentEmotionAssignmentException exception should be thrown")
//    void shouldThrowCommentEmotionAssignmentExceptionWhenPostingAssignmentForWrongEmotion(String emotion) {
//        List<CommentEmotionAssignmentInput> input = List.of(CommentEmotionAssignmentInput.builder()
//                .userId("userId")
//                .commentId("emotionTextId")
//                .emotion(emotion)
//                .build());
//
//        when(userRepository.existsById(anyString())).thenReturn(true);
//
//        assertThatExceptionOfType(CommentEmotionAssignmentException.class)
//                .isThrownBy(() -> underTest.postCommentEmotionAssignments(input))
//                .withMessage(WRONG_EMOTION.getMessage() + " (" + input.get(0).emotion() + ")");
//    }
//
//    @ParameterizedTest
//    @EnumSource(Emotion.class)
//    @DisplayName("when invalid comment id is passed, a CommentException exception should be thrown")
//    void shouldThrowCommentExceptionWhenPostingAssignmentForWrongCommentId(Emotion emotion) {
//        List<CommentEmotionAssignmentInput> input = List.of(CommentEmotionAssignmentInput.builder()
//                .userId("userId")
//                .commentId("emotionTextId")
//                .emotion(emotion.toString())
//                .build());
//
//        when(userRepository.existsById(anyString())).thenReturn(true);
//        when(commentRepository.findByCommentId(anyString())).thenReturn(Optional.empty());
//
//        assertThatExceptionOfType(CommentException.class)
//                .isThrownBy(() -> underTest.postCommentEmotionAssignments(input))
//                .withMessage(NO_COMMENT_WITH_ENTERED_ID.getMessage() + " '" + input.get(0).textId() + "'");
//    }
//
//    @ParameterizedTest
//    @EnumSource(Emotion.class)
//    @DisplayName("when already existing assignment is passe, a CommentEmotionAssignmentException exception should be thrown")
//    void shouldThrowCommentEmotionAssignmentExceptionWhenTryingToDuplicateAssignment(Emotion emotion) {
//        List<CommentEmotionAssignmentInput> input = List.of(CommentEmotionAssignmentInput.builder()
//                .userId("userId")
//                .commentId("emotionTextId")
//                .emotion(emotion.toString())
//                .build());
//        Comment comment = Comment.builder()
//                .commentId("emotionTextId")
//                .content("content")
//                .assignmentsNumber(0)
//                .build();
//        CommentEmotionAssignment assignment = CommentEmotionAssignment.builder()
//                        .userId("userId")
//                        .commentId("emotionTextId")
//                        .build();
//
//        when(userRepository.existsById(anyString())).thenReturn(true);
//        when(commentRepository.findByCommentId(anyString())).thenReturn(Optional.of(comment));
//        when(assignmentRepository.findByUserIdAndCommentId(anyString(), anyString())).thenReturn(Optional.of(assignment));
//
//        assertThatExceptionOfType(CommentEmotionAssignmentException.class)
//                .isThrownBy(() -> underTest.postCommentEmotionAssignments(input))
//                .withMessage(ASSIGNMENT_ALREADY_EXISTS.getMessage() + " (" + input.get(0).textId() + ")");
//    }
//}
