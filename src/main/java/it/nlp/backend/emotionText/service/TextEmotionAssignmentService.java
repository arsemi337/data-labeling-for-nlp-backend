package it.nlp.backend.emotionText.service;

import it.nlp.backend.emotionText.dto.TextEmotionAssignmentInput;
import it.nlp.backend.emotionText.dto.TextEmotionAssignmentOutput;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface TextEmotionAssignmentService {

    List<TextEmotionAssignmentOutput> postTextEmotionAssignments(List<TextEmotionAssignmentInput> assignmentInputs);

    void generateTextEmotionAssignmentsDataset(HttpServletResponse servletResponse);
}
