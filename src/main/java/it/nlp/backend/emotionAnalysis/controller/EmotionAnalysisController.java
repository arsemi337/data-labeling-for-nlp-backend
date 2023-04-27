package it.nlp.backend.emotionAnalysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.nlp.backend.emotionAnalysis.dto.ModelOutput;
import it.nlp.backend.emotionAnalysis.dto.TextEmotionInput;
import it.nlp.backend.emotionAnalysis.dto.TextEmotionOutput;
import it.nlp.backend.emotionAnalysis.service.EmotionAnalysisService;
import it.nlp.backend.emotionAnalysis.service.ModelsStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Tag(name = "Emotion Analysis")
@RequestMapping("/api/v1/emotion")
@PreAuthorize("hasAuthority('ADMIN')")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class EmotionAnalysisController {
    private final EmotionAnalysisService emotionAnalysisService;
    private final ModelsStorageService modelsStorageService;

    EmotionAnalysisController(
            EmotionAnalysisService emotionAnalysisService,
            ModelsStorageService modelsStorageService) {
        this.emotionAnalysisService = emotionAnalysisService;
        this.modelsStorageService = modelsStorageService;
    }

    @PostMapping
    @Operation(summary = "Deduce an emotion from a text")
    ResponseEntity<TextEmotionOutput> deduceTextEmotion(
            @RequestBody TextEmotionInput textEmotionInput) {
        return ResponseEntity.ok(emotionAnalysisService.classifyTextEmotion(textEmotionInput));
    }

    @PostMapping("/model")
    public ResponseEntity<ModelOutput> uploadModel(
            @RequestParam("file") MultipartFile modelZip,
            @RequestParam("modelName") String modelName
    ) {
        return ResponseEntity.ok(modelsStorageService.uploadModel(modelZip, modelName));
    }

    @GetMapping("/model")
    public ResponseEntity<List<ModelOutput>> getModelList() {
        return ResponseEntity.ok(modelsStorageService.getModelList());
    }

    @DeleteMapping("/model")
    public ResponseEntity<Void> removeModel(@RequestParam("modelName") String modelName) {
        modelsStorageService.removeModel(modelName);
        return ResponseEntity.noContent().build();
    }
}