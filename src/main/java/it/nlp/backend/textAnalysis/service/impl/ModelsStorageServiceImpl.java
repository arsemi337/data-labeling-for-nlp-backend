package it.nlp.backend.textAnalysis.service.impl;

import it.nlp.backend.textAnalysis.dto.ModelDetailsOutput;
import it.nlp.backend.textAnalysis.dto.ModelOutput;
import it.nlp.backend.textAnalysis.dto.ModelUploadInput;
import it.nlp.backend.textAnalysis.model.Model;
import it.nlp.backend.textAnalysis.repository.ModelRepository;
import it.nlp.backend.textAnalysis.service.FileService;
import it.nlp.backend.textAnalysis.service.ModelsStorageService;
import it.nlp.backend.textAnalysis.service.TfServingConfigService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static it.nlp.backend.exception.messages.ModelExceptionMessages.MODEL_DOES_NOT_EXIST;

@Service
public class ModelsStorageServiceImpl implements ModelsStorageService {

    private final TfServingConfigService tfServingConfigService;
    private final ModelRepository modelRepository;
    private final FileService fileService;
    private final ModelsStorageValidator validator;
    private final static String TEMP_FILE_PREFIX = "temp";

    @Value("${subprofile.model-destination-path}")
    private String modelDirPath;

    public ModelsStorageServiceImpl(TfServingConfigService tfServingConfigService,
                                    ModelRepository modelRepository,
                                    FileService fileService) {
        this.tfServingConfigService = tfServingConfigService;
        this.modelRepository = modelRepository;
        this.fileService = fileService;
        this.validator = new ModelsStorageValidator();
    }

    @Override
    public ModelOutput uploadModel(MultipartFile modelZip, ModelUploadInput modelUploadInput) {
        String modelName = modelUploadInput.modelName();
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

        modelRepository.save(Model.builder()
                .name(modelName)
                .modelInfo(modelUploadInput.modelInfo())
                .build());

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
    public ModelDetailsOutput getModelDetails(String modelName) {
        Model model = modelRepository.findById(modelName)
                .orElseThrow(() -> new NoSuchElementException(MODEL_DOES_NOT_EXIST.getMessage() + modelName));

        return ModelDetailsOutput.builder()
                .modelName(model.getName())
                .modelInfo(model.getModelInfo())
                .build();
    }

    @Override
    public void removeModel(String modelName) {
        validator.validateModelNameIsNotNullOrEmpty(modelName);
        File modelDir = new File(modelDirPath, modelName);
        validator.validateModelIsDirectoryAndExists(modelDir);
        fileService.removeDirectory(modelDir);
        tfServingConfigService.removeModelFromConfig(modelName, modelDirPath);
        modelRepository.deleteById(modelName);
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