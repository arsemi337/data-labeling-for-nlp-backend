package it.winter2223.bachelor.ak.backend.comment.service.impl;

import it.winter2223.bachelor.ak.backend.comment.dto.CommentOutput;
import it.winter2223.bachelor.ak.backend.comment.persistence.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest {

    CommentMapper commentMapper = new CommentMapper();

    @Test
    @DisplayName("when Comment is provided, it should be mapped to CommentOutput")
    void shouldMapCommentToCommentOutput() {
        Comment comment = getComment();

        CommentOutput commentOutput = commentMapper.mapToCommentOutput(comment);

        assertEquals(commentOutput.commentId(), comment.getCommentId());
        assertEquals(commentOutput.content(), comment.getContent());
    }

    private Comment getComment() {
        return Comment.builder()
                .commentId("testId")
                .content("testContent")
                .isAssigned(false)
                .build();
    }
}
