package it.nlp.backend.emotionAnalysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.nlp.backend.emotionAnalysis.dto.TextEmotionInput;
import it.nlp.backend.emotionAnalysis.dto.TextEmotionOutput;
import it.nlp.backend.emotionAnalysis.service.EmotionAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Emotion Analysis")
@RequestMapping("/api/v1/emotion")
@PreAuthorize("hasAuthority('ADMIN')")
@SecurityRequirement(name = "Bearer Authentication")
public class EmotionAnalysisController {
    private final EmotionAnalysisService emotionAnalysisService;

    EmotionAnalysisController(EmotionAnalysisService emotionAnalysisService) {
        this.emotionAnalysisService = emotionAnalysisService;
    }

    @PostMapping
    @Operation(summary = "Deduce an emotion from a text")
    ResponseEntity<TextEmotionOutput> deduceTextEmotion(
            @RequestBody TextEmotionInput textEmotionInput) {
        return ResponseEntity.ok(emotionAnalysisService.classifyTextEmotion(textEmotionInput));
    }
}
