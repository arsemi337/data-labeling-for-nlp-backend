//package it.nlp.backend.text.service.impl;
//
//import it.winter2223.bachelor.ak.backend.text.dto.CommentOutput;
//import it.winter2223.bachelor.ak.backend.text.model.Comment;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class EmotionTextMapperTest {
//
//    CommentMapper commentMapper = new CommentMapper();
//
//    @Test
//    @DisplayName("when Comment is provided, it should be mapped to CommentOutput")
//    void shouldMapCommentToCommentOutput() {
//        Comment text = getComment();
//
//        CommentOutput commentOutput = commentMapper.mapToCommentOutput(text);
//
//        assertEquals(commentOutput.commentId(), text.getCommentId());
//        assertEquals(commentOutput.content(), text.getContent());
//    }
//
//    private Comment getComment() {
//        return Comment.builder()
//                .commentId("testId")
//                .content("testContent")
//                .build();
//    }
//}
