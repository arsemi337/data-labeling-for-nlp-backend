package it.nlp.backend.emotionText.service;

import it.nlp.backend.emotionText.dto.TextEmotionAssignmentInput;
import it.nlp.backend.emotionText.dto.TextEmotionAssignmentOutput;
import it.nlp.backend.emotionText.dto.TextEmotionAssignmentsNumberOutput;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface TextEmotionAssignmentService {

    List<TextEmotionAssignmentOutput> postTextEmotionAssignments(String userEmail,
                                                                 List<TextEmotionAssignmentInput> assignmentInputs);

    TextEmotionAssignmentsNumberOutput getNumberOfTextEmotionAssignmentsForUser(String userEmail);

    void generateTextEmotionAssignmentsDataset(HttpServletResponse servletResponse);
}
