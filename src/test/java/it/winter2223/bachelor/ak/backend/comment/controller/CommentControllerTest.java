package it.winter2223.bachelor.ak.backend.comment.controller;

import it.winter2223.bachelor.ak.backend.comment.dto.CommentOutput;
import it.winter2223.bachelor.ak.backend.comment.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
    CommentService commentService;

    @Test
    @DisplayName("when correct pageable passed, list of comments should be returned")
    void shouldFetchCommentsList() throws Exception {
        UUID commentId = UUID.randomUUID();
        PageImpl<CommentOutput> mockedValue = new PageImpl<>(List.of(getCommentOutput(commentId)));
        int pageNumber = 0;
        int pageSize = 1;
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        when(commentService.fetchCommentsList(pageRequest)).thenReturn(mockedValue);
        CommentOutput commentOutput = getCommentOutput(commentId);


        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/comment?page=" + pageNumber + "&size=" + pageSize))
                .andExpect(jsonPath("$.content.[0].commentId", equalTo(commentOutput.commentId().toString())))
                .andExpect(jsonPath("$.content.[0].content", equalTo("Test content")));
        verify(commentService).fetchCommentsList(pageRequest);
    }

    private CommentOutput getCommentOutput(UUID commentId) {
        return CommentOutput.builder()
                .commentId(commentId)
                .content("Test content")
                .build();
    }
}