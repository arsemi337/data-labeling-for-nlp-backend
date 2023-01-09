package it.winter2223.bachelor.ak.backend.emotionAnalysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.winter2223.bachelor.ak.backend.emotionAnalysis.dto.CommentEmotionInput;
import it.winter2223.bachelor.ak.backend.emotionAnalysis.dto.CommentEmotionOutput;
import it.winter2223.bachelor.ak.backend.emotionAnalysis.service.EmotionAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Emotion Analysis")
@RequestMapping("/api/v1/emotion")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Profile("NLPEnabled")
public class EmotionAnalysisController {
    private final EmotionAnalysisService emotionAnalysisService;

    @PostMapping
    @Operation(summary = "Deduce an emotion from a comment")
    @PreAuthorize("hasAuthority('USER_READ_WRITE')")
    ResponseEntity<CommentEmotionOutput> deduceCommentEmotion(
            @RequestBody CommentEmotionInput commentEmotionInput) {
        return ResponseEntity.ok(emotionAnalysisService.classifyCommentEmotion(commentEmotionInput));
    }
}
