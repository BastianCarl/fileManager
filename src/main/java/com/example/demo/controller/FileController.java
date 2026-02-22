package com.example.demo.controller;

import com.example.demo.files.FileServiceOrchestrator;
import com.example.demo.service.FileMetaDataService;
import com.example.demo.service.UserService;
import com.example.demo.utility.Archiver;
import com.example.demo.utility.FileHelper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping({"/files"})
@AllArgsConstructor
public class FileController {

    @Value("${archive.name}")
    private String archiveName;
    private final Archiver archiver;
    private final FileServiceOrchestrator fileServiceOrchestrator;
    private final UserService userService;
    private final FileMetaDataService fileMetaDataService;
    private final FileHelper fileHelper;

    @Autowired
    public FileController(
            Archiver archiver,
            FileServiceOrchestrator fileServiceOrchestrator,
            UserService userService,
            FileMetaDataService fileMetaDataService,
            FileHelper fileHelper
    )
    {
        this.archiver = archiver;
        this.fileServiceOrchestrator = fileServiceOrchestrator;
        this.userService = userService;
        this.fileMetaDataService = fileMetaDataService;
        this.fileHelper = fileHelper;
    }

    @PostMapping()
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader("Authentication") String authToken) throws IOException {
        if (fileMetaDataService.checkFileMetadataExists(fileHelper.sha256Hex(file.getInputStream()))) {
            fileServiceOrchestrator.upload(file, userService.getOwnerId(authToken));
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.ok().body("Duplicated file");
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

    @PostMapping("/restore")
    public void restoreBackup(@RequestParam("restoreDate") String date, @RequestHeader("Authentication") String authToken){
        fileServiceOrchestrator.restoreBackup(date, userService.getOwnerId(authToken));
    }
}