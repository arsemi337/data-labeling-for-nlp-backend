package it.nlp.backend.textAnalysis.dto;

import lombok.Builder;

@Builder
public record ModelDetailsOutput(
        String modelName,
        String modelInfo
) {
}
