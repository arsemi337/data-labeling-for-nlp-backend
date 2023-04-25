//package it.winter2223.bachelor.ak.backend.text.service.impl;
//
//import com.google.api.services.youtube.model.*;
//import it.winter2223.bachelor.ak.backend.text.repository.CommentRepository;
//import it.winter2223.bachelor.ak.backend.text.service.YouTubeService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class YouTubeEmotionTextServiceImplTest {
//
//    @InjectMocks
//    YouTubeCommentServiceImpl underTest;
//
//    @Mock
//    CommentRepository commentRepository;
//
//    @Mock
//    YouTubeService youTubeService;
//
//    @Test
//    @DisplayName("should fetch from youtube one text")
//    void shouldFetchCommentsOfPopularVideos() {
//        VideoListResponse ytVideos = new VideoListResponse();
//        Video ytVideo = new Video();
//        ytVideo.setId("videoId");
//        ytVideos.setItems(List.of(ytVideo));
//
//        CommentThreadListResponse ytComments = getCommentThreadListResponse("To jest przykładowy komentarz po polsku, który ma co najmniej pięć słów.");
//
//
//        when(youTubeService.fetchIdsOfMostPopularVideos()).thenReturn(ytVideos);
//        when(youTubeService.fetchMostPopularComments(anyString())).thenReturn(ytComments);
//        when(commentRepository.findById(anyString())).thenReturn(Optional.empty());
//
//        List<it.winter2223.bachelor.ak.backend.text.model.Comment> comments
//                = underTest.fetchInternetComments();
//
//        assertEquals(1, comments.size());
//        assertEquals("emotionTextId", comments.get(0).getCommentId());
//        assertEquals("To jest przykładowy komentarz po polsku, który ma co najmniej pięć słów.", comments.get(0).getContent());
//        verify(youTubeService).fetchIdsOfMostPopularVideos();
//        verify(youTubeService).fetchMostPopularComments(anyString());
//        verify(commentRepository).findById(anyString());
//    }
//
//    @Test
//    @DisplayName("should fetch from youtube a text without html tags")
//    void shouldFetchCommentWithRemovedHtmlTags() {
//        VideoListResponse ytVideos = new VideoListResponse();
//        Video ytVideo = new Video();
//        ytVideo.setId("videoId");
//        ytVideos.setItems(List.of(ytVideo));
//
//        CommentThreadListResponse ytComments = getCommentThreadListResponse("<p>To jest przykładowy komentarz z tagiem html</p>");
//
//
//        when(youTubeService.fetchIdsOfMostPopularVideos()).thenReturn(ytVideos);
//        when(youTubeService.fetchMostPopularComments(anyString())).thenReturn(ytComments);
//        when(commentRepository.findById(anyString())).thenReturn(Optional.empty());
//
//        List<it.winter2223.bachelor.ak.backend.text.model.Comment> comments
//                = underTest.fetchInternetComments();
//
//        assertEquals(1, comments.size());
//        assertEquals("emotionTextId", comments.get(0).getCommentId());
//        assertEquals("To jest przykładowy komentarz z tagiem html", comments.get(0).getContent());
//        verify(youTubeService).fetchIdsOfMostPopularVideos();
//        verify(youTubeService).fetchMostPopularComments(anyString());
//        verify(commentRepository).findById(anyString());
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings = {
//            "This is an example of a non-polish text with a proper words number",
//            "Zbyt krótki komentarz",
//            "Zbyt długi komentarz, ma ponad 250 słów, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz"
//    })
//    @DisplayName("should not fetch from youtube any text due to their wrong language or words number")
//    void shouldNotFetchAnyCommentsDueToWrongLanguageOrWordsNumber(String commentContent) {
//        VideoListResponse ytVideos = new VideoListResponse();
//        Video ytVideo = new Video();
//        ytVideo.setId("videoId");
//        ytVideos.setItems(List.of(ytVideo));
//
//        CommentThreadListResponse ytComments = getCommentThreadListResponse(commentContent);
//
//        when(youTubeService.fetchIdsOfMostPopularVideos()).thenReturn(ytVideos);
//        when(youTubeService.fetchMostPopularComments(anyString())).thenReturn(ytComments);
//        when(commentRepository.findById(anyString())).thenReturn(Optional.empty());
//
//        List<it.winter2223.bachelor.ak.backend.text.model.Comment> comments
//                = underTest.fetchInternetComments();
//
//        assertEquals(0, comments.size());
//        verify(youTubeService).fetchIdsOfMostPopularVideos();
//        verify(youTubeService).fetchMostPopularComments(anyString());
//        verify(commentRepository).findById(anyString());
//    }
//
//    private CommentThreadListResponse getCommentThreadListResponse(String commentContent) {
//        CommentThreadListResponse ytComments = new CommentThreadListResponse();
//        CommentThread ytCommentThread = new CommentThread();
//        CommentThreadSnippet ytCommentThreadSnippet = new CommentThreadSnippet();
//        Comment ytComment = new Comment();
//        CommentSnippet ytCommentSnippet = new CommentSnippet();
//
//        ytComment.setId("emotionTextId");
//        ytCommentSnippet.setTextDisplay(commentContent);
//        ytComment.setSnippet(ytCommentSnippet);
//        ytCommentThreadSnippet.setTopLevelComment(ytComment);
//        ytCommentThread.setSnippet(ytCommentThreadSnippet);
//        ytComments.setItems(List.of(ytCommentThread));
//        return ytComments;
//    }
//}
