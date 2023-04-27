package it.nlp.backend.emotionAnalysis.service.impl;

import com.google.common.io.Files;
import it.nlp.backend.emotionAnalysis.dto.ModelOutput;
import it.nlp.backend.emotionAnalysis.service.ModelsStorageService;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static it.nlp.backend.exception.messages.ModelExceptionMessages.*;

@Service
@Slf4j
public class ModelsStorageServiceImpl implements ModelsStorageService {

    private final static String TEMP_FILE_PREFIX = "temp";

    @Value("${subprofile.model-destination-path}")
    private String modelDirPath;

    @Override
    public ModelOutput uploadModel(MultipartFile modelZip, String modelName) {
        validateModelNameIsNotNullOrEmpty(modelName);
        try {
            String zipOriginalFilenameWithoutExtension = FilenameUtils.removeExtension(modelZip.getOriginalFilename());
            if (zipOriginalFilenameWithoutExtension == null || zipOriginalFilenameWithoutExtension.isEmpty()) {
                throw new IllegalArgumentException(MODEL_ZIP_NAME_IS_NULL.getMessage());
            }

            File zip = File.createTempFile(UUID.randomUUID().toString(), TEMP_FILE_PREFIX);
            FileOutputStream o = new FileOutputStream(zip);
            IOUtils.copy(modelZip.getInputStream(), o);
            o.close();

            try (ZipFile zipFile = new ZipFile(zip)) {
                File extractedModelDir = new File(modelDirPath, zipOriginalFilenameWithoutExtension);
                File modelDirDestination = new File(modelDirPath, modelName);
                if (modelDirDestination.exists()) {
                    throw new IllegalArgumentException(MODEL_ALREADY_EXISTS.getMessage() + modelName);
                }
                zipFile.extractAll(modelDirPath);
                Files.move(extractedModelDir, modelDirDestination); // change name of the model name to the one specified
            } catch (ZipException e) {
                throw new IllegalStateException(FILE_CANNOT_BE_UNZIPPED.getMessage());
            } finally {
                if (!zip.delete()) {
                    log.error("Zip file in temp directory was not deleted: " + zip.getName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(UNEXPECTED_ERROR.getMessage());
        }
        return ModelOutput.builder()
                .modelName(modelZip.getName())
                .path(modelDirPath)
                .build();
    }

    @Override
    public List<ModelOutput> getModelList() {
        File modelDir = new File(modelDirPath);
        File[] modelDirFiles = modelDir.listFiles();
        if (modelDirFiles == null) {
            throw new IllegalStateException(PATH_IS_NOT_DIRECTORY.getMessage() + modelDirPath);
        }

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
        validateModelNameIsNotNullOrEmpty(modelName);
        File modelDir = new File(modelDirPath, modelName);
        if (modelDir.exists() && modelDir.isDirectory()) {
            try {
                FileUtils.deleteDirectory(modelDir);
            } catch (IOException e) {
                throw new IllegalStateException(MODEL_REMOVAL_FAILED.getMessage() + modelDir.getAbsolutePath());
            }
        } else {
            throw new IllegalArgumentException(MODEL_DOES_NOT_EXIST.getMessage() + modelDir.getAbsolutePath());
        }
    }

    private void validateModelNameIsNotNullOrEmpty(String modelName) {
        if (modelName == null || modelName.isEmpty()) {
            throw new IllegalArgumentException(MODEL_NAME_IS_NULL.getMessage());
        }
    }
}
