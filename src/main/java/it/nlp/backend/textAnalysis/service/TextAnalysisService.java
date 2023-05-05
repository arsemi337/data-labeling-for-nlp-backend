package it.nlp.backend.textAnalysis.service;

import it.nlp.backend.textAnalysis.dto.TextInput;
import it.nlp.backend.textAnalysis.dto.TextPredictionOutput;

public interface TextAnalysisService {
    TextPredictionOutput getTextPredictions(TextInput textInput);
}
