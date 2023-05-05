package it.nlp.backend.textAnalysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.nlp.backend.textAnalysis.dto.*;
import it.nlp.backend.textAnalysis.service.TextAnalysisService;
import it.nlp.backend.textAnalysis.service.ModelsStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Tag(name = "Text Analysis")
@RequestMapping("/api/v1/text")
@PreAuthorize("hasAuthority('ADMIN')")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class TextAnalysisController {
    private final TextAnalysisService textAnalysisService;
    private final ModelsStorageService modelsStorageService;

    TextAnalysisController(
            TextAnalysisService textAnalysisService,
            ModelsStorageService modelsStorageService) {
        this.textAnalysisService = textAnalysisService;
        this.modelsStorageService = modelsStorageService;
    }

    @PostMapping
    @Operation(summary = "Get prediction from a text")
    ResponseEntity<TextPredictionOutput> getPrediction(
            @RequestBody TextInput textInput) {
        return ResponseEntity.ok(textAnalysisService.getTextPredictions(textInput));
    }

    @PostMapping("/model")
    public ResponseEntity<ModelOutput> uploadModel(
            @RequestPart("file") MultipartFile modelZip,
            @RequestPart("modelUploadInput") ModelUploadInput modelUploadInput
    ) {
        return ResponseEntity.ok(modelsStorageService.uploadModel(modelZip, modelUploadInput));
    }

    @GetMapping("/model")
    public ResponseEntity<List<ModelOutput>> getModelList() {
        return ResponseEntity.ok(modelsStorageService.getModelList());
    }

    @GetMapping("/model/{modelName}")
    public ResponseEntity<ModelDetailsOutput> getModelDetails(
            @PathVariable String modelName
    ) {
        return ResponseEntity.ok(modelsStorageService.getModelDetails(modelName));
    }

    @DeleteMapping("/model")
    public ResponseEntity<Void> removeModel(@RequestParam("modelName") String modelName) {
        modelsStorageService.removeModel(modelName);
        return ResponseEntity.noContent().build();
    }
}