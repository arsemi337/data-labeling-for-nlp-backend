package it.nlp.backend.textAnalysis.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class PredictionInput {
    private List<String> instances;
}
