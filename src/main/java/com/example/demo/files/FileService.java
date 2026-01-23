package com.example.demo.files;

import com.example.demo.model.FileMetadata;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

public interface FileService {
    void uploadFile(MultipartFile file) throws IOException;
    void uploadFile(File file) throws IOException;
     byte[] downloadFile(FileMetadata fileMetadata) throws IOException;

     default String generateKey(MultipartFile file) {
        return file.getOriginalFilename().split("\\.")[0] + file.getSize();
    }

    default String generateKey(File file) {
        return file.getName().split("\\.")[0] + file.length();
    }
    default String generateKey(FileMetadata file) {
        return file.getName().split("\\.")[0] + file.getSize();
    }

}
