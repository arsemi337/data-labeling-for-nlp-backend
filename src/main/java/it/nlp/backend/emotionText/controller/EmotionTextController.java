package it.nlp.backend.emotionText.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.nlp.backend.emotionText.controller.swagger.EmotionTextSwaggerSample;
import it.nlp.backend.emotionText.dto.EmotionTextOutput;
import it.nlp.backend.emotionText.service.EmotionTextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@Tag(name = "Emotion text")
@RequestMapping("/api/v1/emotionTexts")
@SecurityRequirement(name = "Bearer Authentication")
class EmotionTextController {
    private final EmotionTextService emotionTextService;

    EmotionTextController(EmotionTextService emotionTextService) {
        this.emotionTextService = emotionTextService;
    }

    @GetMapping("/youtube")
    @Operation(summary = "Fetch comments from You Tube")
    ResponseEntity<List<EmotionTextOutput>> fetchYTComments() {
        return ResponseEntity.ok(emotionTextService.fetchYTComments());
    }

    @GetMapping("/all")
    @Operation(summary = "Fetch list of emotion texts from the database (with pagination)")
    ResponseEntity<Page<EmotionTextOutput>> fetchEmotionTexts(
            @Parameter(example = EmotionTextSwaggerSample.PAGEABLE_EXAMPLE)
            Pageable pageable) {
        return ResponseEntity.ok(emotionTextService.fetchEmotionTexts(pageable));
    }

    @GetMapping
    @Operation(summary = "Fetch emotion texts to be assigned by the user with userId")
    ResponseEntity<List<EmotionTextOutput>> fetchEmotionTextsToBeAssigned(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "emotionTextsNumber") String emotionTextsNumber) {
        return ResponseEntity.ok(emotionTextService.fetchEmotionTextsToBeAssigned(userId, emotionTextsNumber));
    }
}
