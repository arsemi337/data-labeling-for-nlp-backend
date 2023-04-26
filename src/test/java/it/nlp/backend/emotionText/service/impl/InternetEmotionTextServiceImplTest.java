package it.nlp.backend.emotionText.service.impl;

import com.google.api.services.youtube.model.*;
import it.nlp.backend.emotionText.model.EmotionText;
import it.nlp.backend.emotionText.repository.EmotionTextRepository;
import it.nlp.backend.emotionText.service.YouTubeService;
import it.nlp.backend.utils.TimeSupplier;
import it.nlp.backend.youTube.model.Channel;
import it.nlp.backend.youTube.repository.ChannelRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InternetEmotionTextServiceImplTest {

    @InjectMocks
    InternetEmotionTextServiceImpl underTest;
    @Mock
    EmotionTextRepository textRepository;
    @Mock
    ChannelRepository channelRepository;
    @Mock
    TimeSupplier timeSupplier;
    @Mock
    YouTubeService youTubeService;

    @Test
    @DisplayName("should fetch from youtube trending list one text")
    void shouldFetchYTCommentsFromPopularVideos() {
        List<String> videoIdList = List.of("videoId");
        CommentThreadListResponse ytComments = getCommentThreadListResponse("To jest przykładowy komentarz po polsku, który ma co najmniej pięć słów.");

        when(youTubeService.fetchIdsOfMostPopularVideos()).thenReturn(videoIdList);
        when(youTubeService.fetchMostPopularComments(anyString())).thenReturn(ytComments);
        when(textRepository.existsByOriginalSourceId(anyString())).thenReturn(false);
        when(timeSupplier.get()).thenReturn(LocalDateTime.of(2000, 10, 10, 10, 10, 10));

        List<EmotionText> emotionTexts  = underTest.fetchYTCommentsFromPopularVideos();

        assertEquals(1, emotionTexts.size());
        assertEquals("emotionTextId", emotionTexts.get(0).getOriginalSourceId());
        assertEquals("To jest przykładowy komentarz po polsku, który ma co najmniej pięć słów.",
                emotionTexts.get(0).getContent());
        verify(youTubeService).fetchIdsOfMostPopularVideos();
        verify(youTubeService).fetchMostPopularComments(anyString());
        verify(textRepository).existsByOriginalSourceId(anyString());
        verify(timeSupplier).get();
    }

    @Test
    @DisplayName("should fetch one comment from saved channel")
    void shouldFetchYTCommentsFromVideosOfSavedChannels() {
        Channel channel = Channel.builder()
                .uploadPlaylistId("playlistId")
                .build();
        List<String> videoIdList = List.of("videoId");
        CommentThreadListResponse ytComments = getCommentThreadListResponse("To jest przykładowy komentarz po polsku, który ma co najmniej pięć słów.");

        when(channelRepository.findAll()).thenReturn(List.of(channel));
        when(youTubeService.fetchIdsOfNewestChannelVideos(channel.getUploadPlaylistId())).thenReturn(videoIdList);
        when(youTubeService.fetchMostPopularComments(anyString())).thenReturn(ytComments);
        when(textRepository.existsByOriginalSourceId(anyString())).thenReturn(false);
        when(timeSupplier.get()).thenReturn(LocalDateTime.of(2000, 10, 10, 10, 10, 10));


        List<EmotionText> emotionTexts = underTest.fetchYTCommentsFromVideosOfSavedChannels();

        assertEquals(1, emotionTexts.size());
        assertEquals("emotionTextId", emotionTexts.get(0).getOriginalSourceId());
        assertEquals("To jest przykładowy komentarz po polsku, który ma co najmniej pięć słów.",
                emotionTexts.get(0).getContent());
        verify(channelRepository).findAll();
        verify(youTubeService).fetchIdsOfNewestChannelVideos(channel.getUploadPlaylistId());
        verify(youTubeService).fetchMostPopularComments(anyString());
        verify(textRepository).existsByOriginalSourceId(anyString());
        verify(timeSupplier).get();
    }

    @Test
    @DisplayName("should fetch from youtube a text without html tags")
    void shouldFetchCommentWithRemovedHtmlTags() {
        List<String> videoIdList = List.of("videoId");
        CommentThreadListResponse ytComments = getCommentThreadListResponse("<p>To jest przykładowy komentarz z tagiem html</p>");

        when(youTubeService.fetchIdsOfMostPopularVideos()).thenReturn(videoIdList);
        when(youTubeService.fetchMostPopularComments(anyString())).thenReturn(ytComments);
        when(textRepository.existsByOriginalSourceId(anyString())).thenReturn(false);
        when(timeSupplier.get()).thenReturn(LocalDateTime.of(2000, 10, 10, 10, 10, 10));

        List<EmotionText> emotionTexts = underTest.fetchYTCommentsFromPopularVideos();

        assertEquals(1, emotionTexts.size());
        assertEquals("emotionTextId", emotionTexts.get(0).getOriginalSourceId());
        assertEquals("To jest przykładowy komentarz z tagiem html", emotionTexts.get(0).getContent());
        verify(youTubeService).fetchIdsOfMostPopularVideos();
        verify(youTubeService).fetchMostPopularComments(anyString());
        verify(textRepository).existsByOriginalSourceId(anyString());
        verify(timeSupplier).get();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "This is an example of a non-polish text with a proper words number",
            "Zbyt krótki komentarz",
            "Zbyt długi komentarz, ma ponad 250 słów, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz, zbyt długi komentarz"
    })
    @DisplayName("should not fetch from youtube any text due to their wrong language or words number")
    void shouldNotFetchAnyCommentsDueToWrongLanguageOrWordsNumber(String commentContent) {
        List<String> videoIdList = List.of("videoId");
        CommentThreadListResponse ytComments = getCommentThreadListResponse(commentContent);

        when(youTubeService.fetchIdsOfMostPopularVideos()).thenReturn(videoIdList);
        when(youTubeService.fetchMostPopularComments(anyString())).thenReturn(ytComments);
        when(textRepository.existsByOriginalSourceId(anyString())).thenReturn(false);

        List<EmotionText> emotionTexts = underTest.fetchYTCommentsFromPopularVideos();

        assertEquals(0, emotionTexts.size());
        verify(youTubeService).fetchIdsOfMostPopularVideos();
        verify(youTubeService).fetchMostPopularComments(anyString());
        verify(textRepository).existsByOriginalSourceId(anyString());
        verify(timeSupplier, never()).get();
    }

    private CommentThreadListResponse getCommentThreadListResponse(String commentContent) {
        CommentThreadListResponse ytComments = new CommentThreadListResponse();
        CommentThread ytCommentThread = new CommentThread();
        CommentThreadSnippet ytCommentThreadSnippet = new CommentThreadSnippet();
        Comment ytComment = new Comment();
        CommentSnippet ytCommentSnippet = new CommentSnippet();

        ytComment.setId("emotionTextId");
        ytCommentSnippet.setTextDisplay(commentContent);
        ytComment.setSnippet(ytCommentSnippet);
        ytCommentThreadSnippet.setTopLevelComment(ytComment);
        ytCommentThread.setSnippet(ytCommentThreadSnippet);
        ytComments.setItems(List.of(ytCommentThread));
        return ytComments;
    }
}
