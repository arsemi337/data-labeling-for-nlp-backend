package it.winter2223.bachelor.ak.backend.emotionText.service;

import it.winter2223.bachelor.ak.backend.emotionText.dto.TextEmotionAssignmentInput;
import it.winter2223.bachelor.ak.backend.emotionText.dto.TextEmotionAssignmentOutput;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface TextEmotionAssignmentService {

    List<TextEmotionAssignmentOutput> postTextEmotionAssignments(List<TextEmotionAssignmentInput> assignmentInputs);

    void generateTextEmotionAssignmentsDataset(HttpServletResponse servletResponse);
}
