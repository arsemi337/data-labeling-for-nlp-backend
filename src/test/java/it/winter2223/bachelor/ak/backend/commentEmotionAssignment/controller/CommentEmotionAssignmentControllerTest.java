package it.winter2223.bachelor.ak.backend.commentEmotionAssignment.controller;

import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.dto.CommentEmotionAssignmentOutput;
import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.dto.EmotionDto;
import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.service.CommentEmotionAssignmentService;
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

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class CommentEmotionAssignmentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CommentEmotionAssignmentService commentEmotionAssignmentService;

    @Test
    @WithMockUser(username="user", authorities={"USER_READ_WRITE"})
    @DisplayName("when correct data is passed, CommentEmotionAssignmentOutput should be returned")
    void shouldPostCommentEmotionAssignment() throws Exception {
        UUID assignmentId = UUID.randomUUID();
        String userId = "userId";
        String commentId = "commentId";

        when(commentEmotionAssignmentService.postCommentEmotionAssignments(anyList()))
                .thenReturn(getAssignmentOutput(assignmentId, userId, commentId));
        List<CommentEmotionAssignmentOutput> assignmentOutput = getAssignmentOutput(assignmentId, userId, commentId);

        mockMvc.perform(getAssignmentPostRequest(userId, commentId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].assignmentId", equalTo(assignmentOutput.get(0).assignmentId().toString())))
                .andExpect(jsonPath("$[0].userId", equalTo(assignmentOutput.get(0).userId())))
                .andExpect(jsonPath("$[0].commentId", equalTo(assignmentOutput.get(0).commentId())))
                .andExpect(jsonPath("$[0].emotionDto", equalTo(EmotionDto.JOY.toString())));
        verify(commentEmotionAssignmentService).postCommentEmotionAssignments(anyList());
    }

    @Test
    @WithMockUser(username="user", authorities={"USER_READ_WRITE"})
    @DisplayName("endpoint for downloading assignments should return 200 status code")
    void shouldDownloadCSVFileWithAssignments() throws Exception {
        doNothing().when(commentEmotionAssignmentService).generateCommentEmotionAssignmentsDataset(any(HttpServletResponse.class));
        mockMvc.perform(getAssignmentGetRequest())
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(commentEmotionAssignmentService).generateCommentEmotionAssignmentsDataset(any(HttpServletResponse.class));
    }

    private MockHttpServletRequestBuilder getAssignmentPostRequest(String userId, String commentId) {
        return MockMvcRequestBuilders.post("/api/v1/assignment")
                .contentType("application/json")
                .content(String.format("""
                        [
                            {
                                "userId": "%s",
                                "commentId": "%s",
                                "emotion": "JOY"
                            }
                        ]
                        """, userId, commentId));
    }

    private List<CommentEmotionAssignmentOutput> getAssignmentOutput(UUID assignmentId, String userId, String commentId) {
        return List.of(CommentEmotionAssignmentOutput.builder()
                .assignmentId(assignmentId)
                .userId(userId)
                .commentId(commentId)
                .emotionDto(EmotionDto.JOY)
                .build());
    }

    private MockHttpServletRequestBuilder getAssignmentGetRequest() {
        return MockMvcRequestBuilders.get("/api/v1/assignment/dataset");
    }
}
