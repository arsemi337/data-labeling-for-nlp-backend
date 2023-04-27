package it.nlp.backend.emotionAnalysis.service;

import it.nlp.backend.emotionAnalysis.dto.ModelOutput;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ModelsStorageService {

    ModelOutput uploadModel(MultipartFile modelZip, String modelName);
    List<ModelOutput> getModelList();
    void removeModel(String modelName);
}
