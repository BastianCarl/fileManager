package com.example.demo.files;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

import com.example.demo.repository.model.FileMetadata;
import com.example.demo.repository.model.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
  void uploadFile(Resource resource);

  byte[] downloadFile(FileMetadata fileMetadata) throws IOException;

  default String generateKey(MultipartFile file) {
    try {
      return file.getOriginalFilename().split("\\.")[0] + sha256Hex(file.getInputStream());
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  default String generateKey(File file) {
    try {
      return file.getName().split("\\.")[0] + sha256Hex(Files.newInputStream(file.toPath()));
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
  }
}
