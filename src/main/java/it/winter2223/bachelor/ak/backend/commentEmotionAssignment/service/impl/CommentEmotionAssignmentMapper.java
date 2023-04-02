package it.winter2223.bachelor.ak.backend.commentEmotionAssignment.service.impl;

import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.dto.CommentEmotionAssignmentOutput;
import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.model.CommentEmotionAssignment;

class CommentEmotionAssignmentMapper {

    private final EmotionMapper emotionMapper;

    CommentEmotionAssignmentMapper() {
        this.emotionMapper = new EmotionMapper();
    }

    CommentEmotionAssignmentOutput mapToCommentEmotionAssignmentOutput(CommentEmotionAssignment commentEmotionAssignment) {
        return CommentEmotionAssignmentOutput.builder()
                .assignmentId(commentEmotionAssignment.getCommentEmotionAssignmentId())
                .userId(commentEmotionAssignment.getUserId())
                .commentId(commentEmotionAssignment.getCommentId())
                .emotionDto(
                        emotionMapper.mapToEmotionDto(
                                commentEmotionAssignment.getEmotion()))
                .build();
    }
}
