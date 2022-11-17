package it.winter2223.bachelor.ak.backend.commentEmotionAssignment.controller;

import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.dto.CommentEmotionAssignmentInput;
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

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

        when(commentEmotionAssignmentService.postCommentEmotionAssignment(any(CommentEmotionAssignmentInput.class)))
                .thenReturn(getAssignmentOutput(assignmentId, userId, commentId));
        CommentEmotionAssignmentOutput assignmentOutput = getAssignmentOutput(assignmentId, userId, commentId);

        mockMvc.perform(getAssignmentPostRequest(userId, commentId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.assignmentId", equalTo(assignmentOutput.assignmentId().toString())))
                .andExpect(jsonPath("$.userId", equalTo(assignmentOutput.userId())))
                .andExpect(jsonPath("$.commentId", equalTo(assignmentOutput.commentId())))
                .andExpect(jsonPath("$.emotionDto", equalTo(EmotionDto.JOY.toString())));
        verify(commentEmotionAssignmentService).postCommentEmotionAssignment(any(CommentEmotionAssignmentInput.class));
    }

    private static MockHttpServletRequestBuilder getAssignmentPostRequest(String userId, String commentId) {
        return MockMvcRequestBuilders.post("/api/v1/assignment")
                .contentType("application/json")
                .content(String.format("""
                        {
                            "userId": "%s",
                            "commentId": "%s",
                            "emotion": "JOY"
                        }
                        """, userId, commentId));
    }

    private CommentEmotionAssignmentOutput getAssignmentOutput(UUID assignmentId, String userId, String commentId) {
        return CommentEmotionAssignmentOutput.builder()
                .assignmentId(assignmentId)
                .userId(userId)
                .commentId(commentId)
                .emotionDto(EmotionDto.JOY)
                .build();
    }
}
