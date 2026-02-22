package com.example.demo.model;


import com.example.demo.files.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

@Component
public class FileMetadataMapper {
    private final FileService fileService;

    @Autowired
    public FileMetadataMapper(FileService fileService) {
        this.fileService = fileService;
    }

    public FileMetadata map(File file, Long ownerId) {
        try {
            return new FileMetadata(
                    file.getName(),
                    Files.probeContentType(file.toPath()),
                    ownerId,
                    file.length(),
                    fileService.generateKey(file),
                    sha256Hex(Files.newInputStream(file.toPath()))
            );
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileMetadata map(MultipartFile file, Long ownerId) {
        try {
            return new FileMetadata(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    ownerId,
                    file.getSize(),
                    fileService.generateKey(file),
                    sha256Hex(file.getInputStream())
            );
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}