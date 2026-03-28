package com.example.demo.service;

import com.example.demo.exception.DatabaseFailure;
import com.example.demo.repository.model.FileMetadata;
import com.example.demo.repository.FileMetadataRepository;
import jakarta.transaction.Transactional;
import java.util.*;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class FileMetaDataService {

  private final FileMetadataRepository repository;

  public boolean checkFileMetadataExists(String hasValue) {
    return repository.existsByHashValue(hasValue);
  }

  public void deleteFileMetaData(FileMetadata file) {
    repository.deleteById(file.getId());
  }

  public List<FileMetadata> getFilesWithLatestVersion() {
    return repository.getFilesWithLatestVersion();
  }

  public List<FileMetadata> getFilesWithAllVersions() {
    return repository.findAll();
  }

  public FileMetadata getFilesMetadata(Long fileId, Long ownerId) {
    List<FileMetadata> files = repository.findByOwnerId(ownerId);
    return files.stream()
        .filter(fileMetadata -> fileMetadata.getId().equals(fileId))
        .toList()
        .getFirst();
  }

  public List<FileMetadata> getImageMetadataList(Long userId) {
    return repository.findByOwnerId(userId);
  }

  @Transactional
  public FileMetadata save(FileMetadata fileMetadata) {
    try {
      return repository.saveWithVersioning(fileMetadata);
    } catch (DataAccessException exception) {
      throw new DatabaseFailure("Failed to save entity", exception);
    }
  }

  private void validateFile(MultipartFile file) {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("File is empty.");
    }

    //        String mimeType = file.getContentType();
    //        if(mimeType == null || !ImagineStoreProperties.allowedMimeTypes.contains(mimeType)) {
    //            throw new IllegalArgumentException("Invalid mime type.");
    //        }
  }
}
