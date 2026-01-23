package com.example.demo.controller;

import com.example.demo.model.FileMetadata;
import com.example.demo.files.FileServiceOrchestrator;
import com.example.demo.service.UserService;
import com.example.demo.utility.Archiver;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping({"/files"})
@AllArgsConstructor
public class FileController {

    @Value("${archive.name}")
    private String archiveName;
    private final Archiver archiver;
    private final FileServiceOrchestrator fileServiceOrchestrator;
    private final UserService userService;

    @Autowired
    public FileController(Archiver archiver, FileServiceOrchestrator fileServiceOrchestrator, UserService userService) {
        this.archiver = archiver;
        this.fileServiceOrchestrator = fileServiceOrchestrator;
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader("Authentication") String authToken) {
        try {
            Long ownerId = userService.getOwnerId(authToken);
            FileMetadata metadata = fileServiceOrchestrator.uploadFile(file, ownerId);
            return ResponseEntity.ok(Map.ofEntries(Map.entry("imageId", Long.toHexString(metadata.getId())), Map.entry("name", metadata.getName()), Map.entry("size", metadata.getSize())));
        } catch (IOException var4) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException var5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable(value = "fileId") Long fileId, @RequestHeader("Authentication") String authToken) throws IOException {
        byte[] bytes = fileServiceOrchestrator.manageDownloadFile(userService.getOwnerId(authToken), fileId);
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                        .filename("ceva")
                        .build()
                        .toString()
                )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    @GetMapping
    public String search(@RequestParam(value = "file") String name) {
        return fileServiceOrchestrator.searchFile(name);
    }

    @GetMapping("/all/{type}")
    public ResponseEntity<byte[]> getAllFiles(@RequestHeader("Authentication") String authToken, @PathVariable String type) throws Exception {
        if (archiver.isArchiveTypeAccepted(type)) {
            byte[] zipBytes = fileServiceOrchestrator.manageDownloadAllFilesAsArchive(userService.getOwnerId(authToken));
            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment()
                                    .filename(archiveName)
                                    .build()
                                    .toString()
                    )
                    .contentType(new MediaType("application", "zip"))
                    .body(zipBytes);
        } else {
            throw new Exception("Unsupported Archive type");
        }
    }
}