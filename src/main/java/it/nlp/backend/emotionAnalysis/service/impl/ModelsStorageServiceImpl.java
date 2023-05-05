package it.nlp.backend.emotionAnalysis.service.impl;

import it.nlp.backend.emotionAnalysis.dto.ModelOutput;
import it.nlp.backend.emotionAnalysis.service.FileService;
import it.nlp.backend.emotionAnalysis.service.ModelsStorageService;
import it.nlp.backend.emotionAnalysis.service.TfServingConfigService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Service
public class ModelsStorageServiceImpl implements ModelsStorageService {

    private final TfServingConfigService tfServingConfigService;
    private final FileService fileService;
    private final ModelsStorageValidator validator;
    private final static String TEMP_FILE_PREFIX = "temp";

    @Value("${subprofile.model-destination-path}")
    private String modelDirPath;

    public ModelsStorageServiceImpl(TfServingConfigService tfServingConfigService, FileService fileService) {
        this.tfServingConfigService = tfServingConfigService;
        this.fileService = fileService;
        this.validator = new ModelsStorageValidator();
    }

    @Override
    public ModelOutput uploadModel(MultipartFile modelZip, String modelName) {
        String zipOriginalFilenameWithoutExtension = FilenameUtils.removeExtension(modelZip.getOriginalFilename());

        validator.validateModelNameIsNotNullOrEmpty(modelName);
        validator.validateModelZipNameIsNotNullOrEmpty(zipOriginalFilenameWithoutExtension);
        validator.validateModelZipIsZip(modelZip);

        File tempZip = fileService.createTempFile(TEMP_FILE_PREFIX);

        fileService.copyMultipartFileToFile(modelZip, tempZip);

        File extractedModelDir = new File(modelDirPath, zipOriginalFilenameWithoutExtension);
        File modelDirDestination = new File(modelDirPath, modelName);

        validator.validateModelDoesNotExist(modelName, modelDirDestination);

        fileService.extractAndDeleteZipFile(tempZip, modelDirPath);

        fileService.moveFile(extractedModelDir, modelDirDestination);

        addModelToConfigFile(modelName);

        return ModelOutput.builder()
                .modelName(modelName)
                .path(modelDirDestination.getAbsolutePath())
                .build();
    }

    @Override
    public List<ModelOutput> getModelList() {
        File[] modelDirFiles = fileService.getFilesInDirectory(modelDirPath);

        return Arrays.stream(modelDirFiles)
                .filter(File::isDirectory)
                .map(file -> ModelOutput.builder()
                        .modelName(file.getName())
                        .path(file.getAbsolutePath())
                        .build())
                .toList();
    }

    @Override
    public void removeModel(String modelName) {
        validator.validateModelNameIsNotNullOrEmpty(modelName);
        File modelDir = new File(modelDirPath, modelName);
        validator.validateModelIsDirectoryAndExists(modelDir);
        fileService.removeDirectory(modelDir);
        tfServingConfigService.removeModelFromConfig(modelName);
    }

    private void addModelToConfigFile(String modelName) {
        try {
            tfServingConfigService.addModelToConfig(modelName, modelDirPath);
        } catch (IllegalStateException e) {
            removeModel(modelName);
            throw e;
        }
    }
}