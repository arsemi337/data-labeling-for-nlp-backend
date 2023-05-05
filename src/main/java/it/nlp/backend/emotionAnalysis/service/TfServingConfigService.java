package it.nlp.backend.emotionAnalysis.service;

public interface TfServingConfigService {

    void addModelToConfig(String modelName, String modelPath);
    void removeModelFromConfig(String modelName);
}
