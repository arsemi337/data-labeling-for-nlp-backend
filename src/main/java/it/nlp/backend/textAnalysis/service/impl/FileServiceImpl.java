package it.nlp.backend.textAnalysis.service.impl;

import it.nlp.backend.textAnalysis.service.FileService;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import static it.nlp.backend.exception.messages.ModelExceptionMessages.*;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private final static String ZIP_FILE_DELETION_ERROR_MESSAGE = "Zip file was not deleted: ";

    @Override
    public void moveFile(File from, File to) {
        try {
            FileUtils.moveDirectory(from, to);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(UNEXPECTED_ERROR_MOVING_FILE.getMessage());
        }
    }

    @Override
    public File createTempFile(String prefix) {
        File tempFile;
        try {
            tempFile = File.createTempFile(UUID.randomUUID().toString(), prefix);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(UNEXPECTED_ERROR_CREATING_TEMP.getMessage());
        }
        return tempFile;
    }

    @Override
    public void copyMultipartFileToFile(MultipartFile multipartFile, File destinationFile) {
        try (FileOutputStream o = new FileOutputStream(destinationFile)) {
            IOUtils.copy(multipartFile.getInputStream(), o);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(UNEXPECTED_ERROR_READING_MULTIPART.getMessage());
        }
    }

    @Override
    public void extractAndDeleteZipFile(File zip, String destinationPath) {
        try (ZipFile zipFile = new ZipFile(zip)) {
            zipFile.extractAll(destinationPath);
        } catch (ZipException e) {
            throw new IllegalStateException(FILE_CANNOT_BE_UNZIPPED.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(UNEXPECTED_ERROR_EXTRACTING.getMessage());
        } finally {
            if (!zip.delete()) {
                log.error(ZIP_FILE_DELETION_ERROR_MESSAGE + zip.getName());
            }
        }
    }

    @Override
    public File[] getFilesInDirectory(String destinationPath) {
        File modelDir = new File(destinationPath);
        File[] modelDirFiles = modelDir.listFiles();
        if (modelDirFiles == null) { // is null if file is not dir or IOException occurs
            throw new IllegalStateException(PATH_IS_NOT_DIRECTORY.getMessage() + destinationPath);
        }
        return modelDirFiles;
    }

    @Override
    public void removeDirectory(File directory) {
        try {
            FileUtils.deleteDirectory(directory);
        } catch (IOException e) {
            throw new IllegalStateException(DIRECTORY_REMOVAL_FAILED.getMessage() + directory.getAbsolutePath());
        }
    }
}
