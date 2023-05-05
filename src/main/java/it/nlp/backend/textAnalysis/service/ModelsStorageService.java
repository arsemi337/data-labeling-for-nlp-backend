package it.nlp.backend.textAnalysis.service;

import it.nlp.backend.textAnalysis.dto.ModelDetailsOutput;
import it.nlp.backend.textAnalysis.dto.ModelOutput;
import it.nlp.backend.textAnalysis.dto.ModelUploadInput;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ModelsStorageService {

    ModelOutput uploadModel(MultipartFile modelZip, ModelUploadInput modelUploadInput);
    List<ModelOutput> getModelList();
    ModelDetailsOutput getModelDetails(String modelName);
    void removeModel(String modelName);
}
