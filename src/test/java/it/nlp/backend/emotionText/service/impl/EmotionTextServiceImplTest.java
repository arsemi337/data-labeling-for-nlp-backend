package it.nlp.backend.emotionText.service.impl;

import it.nlp.backend.authentication.model.User;
import it.nlp.backend.authentication.repository.UserRepository;
import it.nlp.backend.emotionText.dto.EmotionTextOutput;
import it.nlp.backend.emotionText.model.EmotionText;
import it.nlp.backend.emotionText.repository.EmotionTextRepository;
import it.nlp.backend.emotionText.service.InternetEmotionTextService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmotionTextServiceImplTest {

    @InjectMocks
    EmotionTextServiceImpl underTest;

    @Mock
    UserRepository userRepository;

    @Mock
    EmotionTextRepository textRepository;

    @Mock
    InternetEmotionTextService internetCommentService;

    @Test
    @DisplayName("should fetch list of youtube videos and then list of comments for these videos")
    void shouldFetchYTCommentsFromPopularVideos() {
        List<EmotionText> emotionTexts = getEmotionTextList();
        when(internetCommentService.fetchYTCommentsFromPopularVideos())
                .thenReturn(emotionTexts);
        when(textRepository.save(any(EmotionText.class)))
                .thenAnswer(answer -> answer.getArgument(0));

        List<EmotionTextOutput> emotionTextOutputs = underTest.fetchYTCommentsFromPopularVideos();

        assertEquals(2, emotionTextOutputs.size());
        assertEquals(emotionTexts.get(0).getEmotionTextId(), emotionTextOutputs.get(0).emotionTextId());
        assertEquals(emotionTexts.get(0).getContent(), emotionTextOutputs.get(0).content());
        assertEquals(emotionTexts.get(1).getEmotionTextId(), emotionTextOutputs.get(1).emotionTextId());
        assertEquals(emotionTexts.get(1).getContent(), emotionTextOutputs.get(1).content());
        verify(internetCommentService).fetchYTCommentsFromPopularVideos();
        verify(textRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("should fetch list of saved yt channels videos and then list of comments for these videos")
    void shouldFetchYTCommentsFromVideosOfSavedChannels() {
        List<EmotionText> emotionTexts = getEmotionTextList();
        when(internetCommentService.fetchYTCommentsFromVideosOfSavedChannels())
                .thenReturn(emotionTexts);
        when(textRepository.save(any(EmotionText.class)))
                .thenAnswer(answer -> answer.getArgument(0));

        List<EmotionTextOutput> emotionTextOutputs = underTest.fetchYTCommentsFromVideosOfSavedChannels();

        assertEquals(2, emotionTextOutputs.size());
        assertEquals(emotionTexts.get(0).getEmotionTextId(), emotionTextOutputs.get(0).emotionTextId());
        assertEquals(emotionTexts.get(0).getContent(), emotionTextOutputs.get(0).content());
        assertEquals(emotionTexts.get(1).getEmotionTextId(), emotionTextOutputs.get(1).emotionTextId());
        assertEquals(emotionTexts.get(1).getContent(), emotionTextOutputs.get(1).content());
        verify(internetCommentService).fetchYTCommentsFromVideosOfSavedChannels();
        verify(textRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("should fetch list of comments from the database")
    void shouldFetchCommentsList() {
        List<EmotionText> emotionTexts = getEmotionTextList();
        PageImpl<EmotionText> mockerValue = new PageImpl<>(emotionTexts);
        int pageNumber = 0;
        int pageSize = 1;
        PageRequest defaultPageRequest = PageRequest.of(pageNumber, pageSize);
        when(textRepository.findAll(defaultPageRequest)).thenReturn(mockerValue);

        Page<EmotionTextOutput> emotionTextOutputPage = underTest.fetchEmotionTexts(defaultPageRequest);
        EmotionTextOutput emotionTextOutput1 = emotionTextOutputPage.getContent().get(0);
        EmotionTextOutput emotionTextOutput2 = emotionTextOutputPage.getContent().get(1);

        assertEquals(emotionTexts.get(0).getEmotionTextId(), emotionTextOutput1.emotionTextId());
        assertEquals(emotionTexts.get(0).getContent(), emotionTextOutput1.content());
        assertEquals(emotionTexts.get(1).getEmotionTextId(), emotionTextOutput2.emotionTextId());
        assertEquals(emotionTexts.get(1).getContent(), emotionTextOutput2.content());
        verify(textRepository).findAll(defaultPageRequest);
    }

    @Test
    @DisplayName("should fetch list of comments to be assigned by user")
    void shouldFetchCommentsToBeAssigned() {
        List<EmotionText> emotionTexts = getEmotionTextList();
        String userEmail = "userEmail";
        String textsNumber = "2";
        UUID assignedEmotionTextId = UUID.randomUUID();
        User user = User.builder()
                .assignedEmotionTextIds(List.of(assignedEmotionTextId))
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(textRepository.findAll()).thenReturn(emotionTexts);

        List<EmotionTextOutput> commentOutputs = underTest.fetchEmotionTextsToBeAssigned(userEmail, textsNumber);

        assertEquals(2, commentOutputs.size());
        assertEquals(emotionTexts.get(0).getEmotionTextId(), commentOutputs.get(0).emotionTextId());
        assertEquals(emotionTexts.get(0).getContent(), commentOutputs.get(0).content());
        assertEquals(emotionTexts.get(1).getEmotionTextId(), commentOutputs.get(1).emotionTextId());
        assertEquals(emotionTexts.get(1).getContent(), commentOutputs.get(1).content());
        verify(userRepository).findByEmail(userEmail);
        verify(textRepository).findAll();
    }

    private List<EmotionText> getEmotionTextList() {
        return List.of(
                EmotionText.builder()
                        .emotionTextId(UUID.randomUUID())
                        .content("testContent1")
                        .assignedEmotions(new ArrayList<>())
                        .build(),
                EmotionText.builder()
                        .emotionTextId(UUID.randomUUID())
                        .content("testContent2")
                        .assignedEmotions(new ArrayList<>())

                        .build()
        );
    }
}
