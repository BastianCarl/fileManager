package com.example.demo.files;

import com.example.demo.model.FileMetadata;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

public interface FileService {
    void uploadFile(MultipartFile file);
    void uploadFile(File file);
    byte[] downloadFile(FileMetadata fileMetadata) throws IOException;
    default String generateKey(MultipartFile file) {
        try {
            return file.getOriginalFilename().split("\\.")[0] + sha256Hex(file.getInputStream());
        }catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    default String generateKey(File file) {
        try {
            return file.getName().split("\\.")[0] + sha256Hex(Files.newInputStream(file.toPath()));
        }catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}