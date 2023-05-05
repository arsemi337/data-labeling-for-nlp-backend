package it.nlp.backend.textAnalysis.dto;

import lombok.Builder;

@Builder
public record ModelOutput(
        String modelName,
        String path
) {
}
