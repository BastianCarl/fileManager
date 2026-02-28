package com.example.demo.model;

import com.example.demo.service.FileMetaDataService;
import java.util.List;

public enum Option {
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