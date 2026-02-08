package com.example.demo.model;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class Resource {

    @Getter
    private final FileMetadata fileMetadata;
    private final MultipartFile multipartFile;
    private final File file;

    public Resource(MultipartFile multipartFile, FileMetadata fileMetadata) {
        this.multipartFile = multipartFile;
        this.file = null;
        this.fileMetadata = fileMetadata;
    }

    public Resource(File file, FileMetadata fileMetadata) {
        this.file = file;
        this.multipartFile = null;
        this.fileMetadata = fileMetadata;
    }

    public Object getSource() {
        return file != null ? file : multipartFile;
    }
}
