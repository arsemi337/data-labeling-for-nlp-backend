package it.nlp.backend.textAnalysis.service;

public interface TfServingConfigService {

    void addModelToConfig(String modelName, String modelDirPath);
    void removeModelFromConfig(String modelName, String modelDirPath);
}
