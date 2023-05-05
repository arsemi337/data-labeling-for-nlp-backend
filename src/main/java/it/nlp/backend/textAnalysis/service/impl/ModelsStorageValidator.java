package it.nlp.backend.textAnalysis.service.impl;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static it.nlp.backend.exception.messages.ModelExceptionMessages.*;

public class ModelsStorageValidator {

    public void validateModelNameIsNotNullOrEmpty(String modelName) {
        if (modelName == null || modelName.isEmpty()) {
            throw new IllegalArgumentException(MODEL_NAME_IS_NULL.getMessage());
        }
    }

    public void validateModelZipIsZip(MultipartFile modelZip) {
        if (!FilenameUtils.isExtension(modelZip.getOriginalFilename(), "zip")) {
            throw new IllegalArgumentException(MODEL_ZIP_IS_NOT_ZIP.getMessage());
        }
    }

    public void validateModelZipNameIsNotNullOrEmpty(String zipName) {
        if (zipName == null || zipName.isEmpty()) {
            throw new IllegalArgumentException(MODEL_ZIP_NAME_IS_NULL.getMessage());
        }
    }

    public void validateModelDoesNotExist(String modelName, File modelDirDestination) {
        if (modelDirDestination.exists()) {
            throw new IllegalArgumentException(MODEL_ALREADY_EXISTS.getMessage() + modelName);
        }
    }

    public void validateModelIsDirectoryAndExists(File modelDir) {
        if (!modelDir.exists() || !modelDir.isDirectory()) {
            throw new IllegalArgumentException(MODEL_DOES_NOT_EXIST.getMessage() + modelDir.getAbsolutePath());
        }
    }
}
