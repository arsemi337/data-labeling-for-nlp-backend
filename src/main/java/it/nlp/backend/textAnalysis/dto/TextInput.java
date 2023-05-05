package it.nlp.backend.textAnalysis.dto;

import lombok.Builder;

@Builder
public record TextInput(
        String modelName,
        String text) {
}
