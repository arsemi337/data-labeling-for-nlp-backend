package it.winter2223.bachelor.ak.backend.comment.service.impl;

import com.google.api.services.youtube.model.*;
import it.winter2223.bachelor.ak.backend.comment.repository.CommentRepository;
import it.winter2223.bachelor.ak.backend.comment.service.YouTubeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class YouTubeCommentServiceImplTest {

    @InjectMocks
    YouTubeCommentServiceImpl underTest;

    @Mock
    CommentRepository commentRepository;

    @Mock
    YouTubeService youTubeService;

    @Test
    @DisplayName("should fetch from youtube comments from popular videos")
    void shouldFetchCommentsOfPopularVideos() {
        VideoListResponse ytVideos = new VideoListResponse();
        Video ytVideo = new Video();
        ytVideo.setId("videoId");
        ytVideos.setItems(List.of(ytVideo));

        CommentThreadListResponse ytComments = new CommentThreadListResponse();
        CommentThread ytCommentThread = new CommentThread();
        CommentThreadSnippet ytCommentThreadSnippet = new CommentThreadSnippet();
        Comment ytComment = new Comment();
        CommentSnippet ytCommentSnippet = new CommentSnippet();

        ytComment.setId("commentId");
        ytCommentSnippet.setTextDisplay("To jest przykładowy komentarz po polsku, który ma co najmniej pięć słów.");
        ytComment.setSnippet(ytCommentSnippet);
        ytCommentThreadSnippet.setTopLevelComment(ytComment);
        ytCommentThread.setSnippet(ytCommentThreadSnippet);
        ytComments.setItems(List.of(ytCommentThread));


        when(youTubeService.fetchMostPopularYTVideos()).thenReturn(ytVideos);
        when(youTubeService.fetchMostPopularYTComments(anyString())).thenReturn(ytComments);
        when(commentRepository.findById(anyString())).thenReturn(Optional.empty());

        List<it.winter2223.bachelor.ak.backend.comment.persistence.Comment> comments
                = underTest.fetchYTCommentsOfPopularVideos();

        assertEquals(1, comments.size());
        assertEquals("commentId", comments.get(0).getCommentId());
        assertEquals("To jest przykładowy komentarz po polsku, który ma co najmniej pięć słów.", comments.get(0).getContent());
        verify(youTubeService).fetchMostPopularYTVideos();
        verify(youTubeService).fetchMostPopularYTComments(anyString());
        verify(commentRepository).findById(anyString());
    }
}
