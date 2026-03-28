package com.example.demo.repository.model;

import com.example.demo.service.FileMetaDataService;
import java.util.List;

public enum Version {
  all() {
    @Override
    public List<FileMetadata> getFiles(FileMetaDataService service) {
      return service.getFilesWithAllVersions();
    }
  },

  latest() {
    @Override
    public List<FileMetadata> getFiles(FileMetaDataService service) {
      return service.getFilesWithLatestVersion();
    }
  };

  public abstract List<FileMetadata> getFiles(FileMetaDataService service);
}
