package com.example.demo.model;


import com.example.demo.files.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HexFormat;

import static org.apache.commons.codec.digest.DigestUtils.sha256;

@Component
public class FileMetadataFactory {
    private final FileService fileService;

    @Autowired
    public FileMetadataFactory(FileService fileService) {
        this.fileService = fileService;
    }

    public FileMetadata create(Object source, Long ownerId) {
        try {
            return switch (source) {
                case File file -> new FileMetadata(
                        file.getName(),
                        Files.probeContentType(file.toPath()),
                        ownerId,
                        file.length(),
                        fileService.generateKey(file),
                        HexFormat.of().formatHex(sha256(Files.readAllBytes(file.toPath())))
                );
                case MultipartFile file -> new FileMetadata(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        ownerId,
                        file.getSize(),
                        fileService.generateKey(file),
                        HexFormat.of().formatHex(sha256(file.getBytes()))
                );
                default -> throw new IllegalStateException("Unexpected value: " + source);
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}