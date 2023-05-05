package it.nlp.backend.textAnalysis.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record TfPredictionOutput(
        List<List<Float>> predictions
) {
}
