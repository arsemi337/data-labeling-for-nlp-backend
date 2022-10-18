package it.winter2223.bachelor.ak.backend.commentEmotionAssignment.service;

import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.dto.CommentEmotionAssignmentInput;
import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.dto.CommentEmotionAssignmentOutput;

public interface CommentEmotionAssignmentService {

    CommentEmotionAssignmentOutput postCommentEmotionAssignment(CommentEmotionAssignmentInput assignmentInput);
}
