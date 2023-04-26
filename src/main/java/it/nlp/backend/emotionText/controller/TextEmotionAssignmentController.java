package it.nlp.backend.emotionText.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.nlp.backend.emotionText.dto.TextEmotionAssignmentOutput;
import it.nlp.backend.emotionText.dto.TextEmotionAssignmentInput;
import it.nlp.backend.emotionText.dto.TextEmotionAssignmentsNumberOutput;
import it.nlp.backend.emotionText.service.TextEmotionAssignmentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Post text-emotion assignment")
    ResponseEntity<List<TextEmotionAssignmentOutput>> postTextEmotionAssignments(
            Authentication authentication,
            @RequestBody List<TextEmotionAssignmentInput> textEmotionAssignmentInputs) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(textEmotionAssignmentService.postTextEmotionAssignments(
                userEmail,
                textEmotionAssignmentInputs));
    }

    @GetMapping("/count")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Get number of texts labeled by user")
    ResponseEntity<TextEmotionAssignmentsNumberOutput> getNumberOfTextEmotionAssignments(
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(textEmotionAssignmentService.getNumberOfTextEmotionAssignmentsForUser(userEmail));
    }

    @GetMapping("/dataset")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get text-emotion assignments as CSV file")
    void fetchTextEmotionAssignments(HttpServletResponse servletResponse) {
        textEmotionAssignmentService.generateTextEmotionAssignmentsDataset(servletResponse);
    }
}
