package it.nlp.backend.emotionText.controller;

import it.nlp.backend.emotionText.dto.EmotionTextOutput;
import it.nlp.backend.emotionText.service.EmotionTextService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    EmotionTextService emotionTextService;

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    @DisplayName("list of comments from trending youtube videos should be returned")
    void shouldGetYTTrendingVideosComments() throws Exception {
        UUID textId = UUID.randomUUID();

        when(emotionTextService.fetchYTCommentsFromPopularVideos()).thenReturn(List.of(getEmotionTextOutput(textId)));
        EmotionTextOutput emotionTextOutput = getEmotionTextOutput(textId);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/emotion-texts/youtube/trending"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].emotionTextId", equalTo(emotionTextOutput.emotionTextId().toString())))
                .andExpect(jsonPath("$[0].content", equalTo("Test content")));
        verify(emotionTextService).fetchYTCommentsFromPopularVideos();
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    @DisplayName("list of comments from saved youtube channels")
    void shouldGetSavedChannelsVideosComments() throws Exception {
        UUID textId = UUID.randomUUID();

        when(emotionTextService.fetchYTCommentsFromVideosOfSavedChannels()).thenReturn(List.of(getEmotionTextOutput(textId)));
        EmotionTextOutput emotionTextOutput = getEmotionTextOutput(textId);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/emotion-texts/youtube/channels"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].emotionTextId", equalTo(emotionTextOutput.emotionTextId().toString())))
                .andExpect(jsonPath("$[0].content", equalTo("Test content")));
        verify(emotionTextService).fetchYTCommentsFromVideosOfSavedChannels();
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    @DisplayName("when correct pageable passed, list of emotion texts should be returned")
    void shouldFetchEmotionTexts() throws Exception {
        UUID textId = UUID.randomUUID();
        PageImpl<EmotionTextOutput> mockedValue = new PageImpl<>(List.of(getEmotionTextOutput(textId)));
        int pageNumber = 0;
        int pageSize = 1;
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        when(emotionTextService.fetchEmotionTexts(pageRequest)).thenReturn(mockedValue);
        EmotionTextOutput emotionTextOutput = getEmotionTextOutput(textId);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/emotion-texts/all?page=" + pageNumber + "&size=" + pageSize))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content.[0].emotionTextId", equalTo(emotionTextOutput.emotionTextId().toString())))
                .andExpect(jsonPath("$.content.[0].content", equalTo("Test content")));
        verify(emotionTextService).fetchEmotionTexts(pageRequest);
    }

    @Test
    @WithMockUser(username = "userEmail", authorities = {"ADMIN"})
    @DisplayName("when correct parameters are passed, list of emotion texts to be assigned should be returned")
    void shouldFetchEmotionTextsToBeAssigned() throws Exception {
        UUID textId = UUID.randomUUID();
        String userEmail = "userEmail";
        String textsNumber = "1";

        when(emotionTextService.fetchEmotionTextsToBeAssigned(userEmail, textsNumber)).thenReturn(List.of(getEmotionTextOutput(textId)));
        EmotionTextOutput emotionTextOutput = getEmotionTextOutput(textId);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/emotion-texts?emotionTextsNumber=" + textsNumber))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].emotionTextId", equalTo(emotionTextOutput.emotionTextId().toString())))
                .andExpect(jsonPath("$[0].content", equalTo("Test content")));
        verify(emotionTextService).fetchEmotionTextsToBeAssigned(userEmail, textsNumber);
    }

    private EmotionTextOutput getEmotionTextOutput(UUID textId) {
        return EmotionTextOutput.builder()
                .emotionTextId(textId)
                .content("Test content")
                .build();
    }
}
