package it.winter2223.bachelor.ak.backend.emotionText.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.winter2223.bachelor.ak.backend.emotionText.dto.TextEmotionAssignmentInput;
import it.winter2223.bachelor.ak.backend.emotionText.dto.TextEmotionAssignmentOutput;
import it.winter2223.bachelor.ak.backend.emotionText.service.TextEmotionAssignmentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Text emotion assignment")
@RequestMapping("/api/v1/emotion-assignments")
@SecurityRequirement(name = "Bearer Authentication")
public class TextEmotionAssignmentController {

    private final TextEmotionAssignmentService textEmotionAssignmentService;

    TextEmotionAssignmentController(TextEmotionAssignmentService textEmotionAssignmentService) {
        this.textEmotionAssignmentService = textEmotionAssignmentService;
    }

    @PostMapping
    @Operation(summary = "Post text-emotion assignment")
    ResponseEntity<List<TextEmotionAssignmentOutput>> postCommentEmotionAssignments(
            @RequestBody List<TextEmotionAssignmentInput> textEmotionAssignmentInputs) {
        return ResponseEntity.ok(textEmotionAssignmentService.postTextEmotionAssignments(textEmotionAssignmentInputs));
    }

    @GetMapping("/dataset")
    @Operation(summary = "Get text-emotion assignments as CSV file")
    void fetchCommentEmotionAssignments(HttpServletResponse servletResponse) {
        textEmotionAssignmentService.generateTextEmotionAssignmentsDataset(servletResponse);
    }
}
