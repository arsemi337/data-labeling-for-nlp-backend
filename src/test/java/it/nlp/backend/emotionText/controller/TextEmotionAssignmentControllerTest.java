package it.nlp.backend.emotionText.controller;

import it.nlp.backend.emotionText.dto.EmotionDto;
import it.nlp.backend.emotionText.dto.TextEmotionAssignmentInput;
import it.nlp.backend.emotionText.dto.TextEmotionAssignmentOutput;
import it.nlp.backend.emotionText.dto.TextEmotionAssignmentsNumberOutput;
import it.nlp.backend.emotionText.service.TextEmotionAssignmentService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class TextEmotionAssignmentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TextEmotionAssignmentService textEmotionAssignmentService;

    @Test
    @WithMockUser(username = "userEmail", authorities = {"ADMIN"})
    @DisplayName("when correct data is passed, TextEmotionAssignmentOutput should be returned")
    void shouldPostTextEmotionAssignment() throws Exception {
        String userEmail = "userEmail";
        UUID userId = UUID.randomUUID();
        UUID textId = UUID.randomUUID();
        List<TextEmotionAssignmentInput> assignmentInputs = List.of(TextEmotionAssignmentInput.builder()
                .textId(textId.toString())
                .emotion("JOY")
                .build());
        List<TextEmotionAssignmentOutput> assignmentOutput = getAssignmentOutput(userId, textId);

        when(textEmotionAssignmentService.postTextEmotionAssignments(userEmail, assignmentInputs))
                .thenReturn(getAssignmentOutput(userId, textId));

        mockMvc.perform(getAssignmentPostRequest(textId.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].userId", equalTo(assignmentOutput.get(0).userId().toString())))
                .andExpect(jsonPath("$[0].textId", equalTo(assignmentOutput.get(0).textId().toString())))
                .andExpect(jsonPath("$[0].emotionDto", equalTo(EmotionDto.JOY.toString())));
        verify(textEmotionAssignmentService).postTextEmotionAssignments(userEmail, assignmentInputs);
    }

    @Test
    @WithMockUser(username = "userEmail", authorities = {"ADMIN"})
    @DisplayName("when correct data is passed, number of user assignments should be returned")
    void shouldFetchNumberOfUserAssignments() throws Exception {
        String userEmail = "userEmail";
        Integer assignmentsNumber = 10;
        TextEmotionAssignmentsNumberOutput assignmentsNumberOutput = getAssignmentsNumberOutput(assignmentsNumber);

        when(textEmotionAssignmentService.getNumberOfTextEmotionAssignmentsForUser(userEmail))
                .thenReturn(getAssignmentsNumberOutput(assignmentsNumber));

        mockMvc.perform(getUserAssignmentsNumberRequest())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.assignmentsCount", equalTo(assignmentsNumberOutput.assignmentsCount())));
        verify(textEmotionAssignmentService).getNumberOfTextEmotionAssignmentsForUser(userEmail);
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    @DisplayName("endpoint for downloading assignments should return 200 status code")
    void shouldDownloadCSVFileWithAssignments() throws Exception {
        doNothing().when(textEmotionAssignmentService).generateTextEmotionAssignmentsDataset(any(HttpServletResponse.class));
        mockMvc.perform(getAssignmentGetRequest())
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(textEmotionAssignmentService).generateTextEmotionAssignmentsDataset(any(HttpServletResponse.class));
    }

    private MockHttpServletRequestBuilder getAssignmentPostRequest(String textId) {
        return MockMvcRequestBuilders.post("/api/v1/emotion-assignments")
                .contentType("application/json")
                .content(String.format("""
                        [
                            {
                                "textId": "%s",
                                "emotion": "JOY"
                            }
                        ]
                        """, textId));
    }

    private MockHttpServletRequestBuilder getUserAssignmentsNumberRequest() {
        return MockMvcRequestBuilders.get("/api/v1/emotion-assignments/count");
    }

    private MockHttpServletRequestBuilder getAssignmentGetRequest() {
        return MockMvcRequestBuilders.get("/api/v1/emotion-assignments/dataset");
    }

    private List<TextEmotionAssignmentOutput> getAssignmentOutput(UUID userId, UUID textId) {
        return List.of(TextEmotionAssignmentOutput.builder()
                .userId(userId)
                .textId(textId)
                .emotionDto(EmotionDto.JOY)
                .build());
    }

    private TextEmotionAssignmentsNumberOutput getAssignmentsNumberOutput(Integer assignmentsNumber) {
        return TextEmotionAssignmentsNumberOutput.builder()
                .assignmentsCount(assignmentsNumber)
                .build();
    }
}
