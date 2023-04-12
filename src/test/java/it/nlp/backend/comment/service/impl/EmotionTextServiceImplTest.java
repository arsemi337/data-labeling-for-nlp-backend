//package it.nlp.backend.comment.service.impl;
//
//import it.nlp.backend.authentication.repository.UserRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class EmotionTextServiceImplTest {
//
//    @InjectMocks
//    CommentServiceImpl underTest;
//
//    @Mock
//    CommentEmotionAssignmentRepository assignmentRepository;
//
//    @Mock
//    UserRepository userRepository;
//
//    @Mock
//    CommentRepository commentRepository;
//
//    @Mock
//    InternetCommentService internetCommentService;
//
//    @Test
//    @DisplayName("should fetch list of youtube videos and then list of comments for these videos")
//    void shouldFetchListOfYouTubeComments() {
//        List<Comment> comments = getCommentsList();
//
//        when(internetCommentService.fetchInternetComments()).thenReturn(comments);
//        when(commentRepository.save(any())).thenAnswer(answer -> answer.getArgument(0));
//
//        List<CommentOutput> commentOutputs = underTest.fetchYTComments();
//
//        assertEquals(2, commentOutputs.size());
//        assertEquals("testId1", commentOutputs.get(0).commentId());
//        assertEquals("testContent1", commentOutputs.get(0).content());
//        assertEquals("testId2", commentOutputs.get(1).commentId());
//        assertEquals("testContent2", commentOutputs.get(1).content());
//        verify(commentRepository, times(2)).save(any());
//    }
//
//    @Test
//    @DisplayName("should fetch list of comments from the database")
//    void shouldFetchCommentsList() {
//        List<Comment> comments = getCommentsList();
//        PageImpl<Comment> mockerValue = new PageImpl<>(comments);
//        int pageNumber = 0;
//        int pageSize = 1;
//        PageRequest defaultPageRequest = PageRequest.of(pageNumber, pageSize);
//        when(commentRepository.findAll(defaultPageRequest)).thenReturn(mockerValue);
//
//        Page<CommentOutput> commentOutputPage = underTest.fetchCommentsList(defaultPageRequest);
//        CommentOutput commentOutput1 = commentOutputPage.getContent().get(0);
//        CommentOutput commentOutput2 = commentOutputPage.getContent().get(1);
//
//        assertEquals("testId1", commentOutput1.commentId());
//        assertEquals("testContent1", commentOutput1.content());
//        assertEquals("testId2", commentOutput2.commentId());
//        assertEquals("testContent2", commentOutput2.content());
//        verify(commentRepository).findAll(defaultPageRequest);
//    }
//
//    @Test
//    @DisplayName("should fetch list of comments to be assigned by user")
//    void shouldFetchCommentsToBeAssigned() {
//        String userId = "userId";
//        String commentsNumber = "2";
//
//        when(userRepository.existsById(anyString())).thenReturn(true);
//        when(assignmentRepository.findByUserId(anyString())).thenReturn(Collections.emptyList());
//        when(commentRepository.findAll()).thenReturn(getCommentsList());
//
//        List<CommentOutput> commentOutputs = underTest.fetchCommentsToBeAssigned(userId, commentsNumber);
//
//        assertEquals(2, commentOutputs.size());
//        assertEquals("testId2", commentOutputs.get(0).commentId());
//        assertEquals("testContent2", commentOutputs.get(0).content());
//        assertEquals("testId1", commentOutputs.get(1).commentId());
//        assertEquals("testContent1", commentOutputs.get(1).content());
//        verify(userRepository).existsById(anyString());
//        verify(assignmentRepository).findByUserId(anyString());
//        verify(commentRepository).findAll();
//    }
//
//    private List<Comment> getCommentsList() {
//        return List.of(
//                Comment.builder()
//                        .commentId("testId1")
//                        .content("testContent1")
//                        .assignmentsNumber(5)
//                        .build(),
//                Comment.builder()
//                        .commentId("testId2")
//                        .content("testContent2")
//                        .assignmentsNumber(2)
//                        .build()
//        );
//    }
//}
