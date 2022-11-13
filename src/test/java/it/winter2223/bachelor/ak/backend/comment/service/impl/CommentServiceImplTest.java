package it.winter2223.bachelor.ak.backend.comment.service.impl;

import com.google.api.services.youtube.model.VideoListResponse;
import it.winter2223.bachelor.ak.backend.comment.dto.CommentOutput;
import it.winter2223.bachelor.ak.backend.comment.persistence.Comment;
import it.winter2223.bachelor.ak.backend.comment.repository.CommentRepository;
import it.winter2223.bachelor.ak.backend.comment.service.YouTubeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @InjectMocks
    CommentServiceImpl underTest;

    @Mock
    CommentRepository commentRepository;

    @Mock
    YouTubeService youTubeService;

    @Test
    @DisplayName("should fetch list of youtube videos and then list of comments for these videos")
    void shouldFetchListOfYouTubeComments() {
        VideoListResponse ytVideos = new VideoListResponse();
        List<Comment> comments = getCommentsList();

        when(youTubeService.fetchMostPopularYTVideos()).thenReturn(ytVideos);
        when(youTubeService.fetchYTCommentsByVideoIds(any(VideoListResponse.class))).thenReturn(comments);
        when(commentRepository.save(any())).thenAnswer(answer -> answer.getArgument(0));

        List<CommentOutput> commentOutputs = underTest.fetchYTComments();

        assertEquals(2, commentOutputs.size());
        assertEquals("testId1", commentOutputs.get(0).commentId());
        assertEquals("testContent1", commentOutputs.get(0).content());
        assertEquals("testId2", commentOutputs.get(1).commentId());
        assertEquals("testContent2", commentOutputs.get(1).content());
        verify(commentRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("should fetch list of comments from the database")
    void shouldFetchCommentsList() {
        List<Comment> comments = getCommentsList();
        PageImpl<Comment> mockerValue = new PageImpl<>(comments);
        int pageNumber = 0;
        int pageSize = 1;
        PageRequest defaultPageRequest = PageRequest.of(pageNumber, pageSize);
        when(commentRepository.findByIsAssigned(false, defaultPageRequest)).thenReturn(mockerValue);

        Page<CommentOutput> commentOutputPage = underTest.fetchCommentsList(defaultPageRequest);
        CommentOutput commentOutput1 = commentOutputPage.getContent().get(0);
        CommentOutput commentOutput2 = commentOutputPage.getContent().get(1);

        assertEquals("testId1", commentOutput1.commentId());
        assertEquals("testContent1", commentOutput1.content());
        assertEquals("testId2", commentOutput2.commentId());
        assertEquals("testContent2", commentOutput2.content());
        verify(commentRepository).findByIsAssigned(false, defaultPageRequest);
    }

    private List<Comment> getCommentsList() {
        return List.of(
                Comment.builder()
                        .commentId("testId1")
                        .content("testContent1")
                        .isAssigned(false)
                        .build(),
                Comment.builder()
                        .commentId("testId2")
                        .content("testContent2")
                        .isAssigned(false)
                        .build()
        );
    }
}
