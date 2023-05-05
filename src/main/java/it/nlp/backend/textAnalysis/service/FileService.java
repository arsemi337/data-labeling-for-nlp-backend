package it.nlp.backend.textAnalysis.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface FileService {

    void moveFile(File from, File to);
    File createTempFile(String prefix);
    void copyMultipartFileToFile(MultipartFile multipartFile, File destinationFile);
    void extractAndDeleteZipFile(File zip, String destinationPath);
    File[] getFilesInDirectory(String destinationPath);
    void removeDirectory(File directory);
}
