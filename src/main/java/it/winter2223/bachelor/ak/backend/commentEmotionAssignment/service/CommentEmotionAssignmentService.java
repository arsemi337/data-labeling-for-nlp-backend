package it.winter2223.bachelor.ak.backend.commentEmotionAssignment.service;

import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.dto.CommentEmotionAssignmentInput;
import it.winter2223.bachelor.ak.backend.commentEmotionAssignment.dto.CommentEmotionAssignmentOutput;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface CommentEmotionAssignmentService {

    List<CommentEmotionAssignmentOutput> postCommentEmotionAssignment(List<CommentEmotionAssignmentInput> assignmentInputs);

    void generateCommentEmotionAssignmentsDataset(HttpServletResponse servletResponse);
}
