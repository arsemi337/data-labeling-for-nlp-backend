package it.nlp.backend.textAnalysis.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record TextPredictionOutput(
        List<Float> predictions
) {
}
