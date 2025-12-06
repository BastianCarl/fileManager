package com.example.demo.controller;

import com.example.demo.model.FileMetadata;
import com.example.demo.repository.UserRepo;
import com.example.demo.service.FileMetaDataService;
import com.example.demo.service.JWTService;
import com.example.demo.files.FileServiceOrchestrator;
import com.example.demo.utility.Archiver;
import com.example.demo.utility.JwtHelper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping({"/files"})
@AllArgsConstructor
public class FileController {

    @Value("${archive.name}")
    private String archiveName;
    private final FileMetaDataService fileMetaDataService;
    private final JWTService jwtService;
    private final UserRepo userRepo;
    private final Archiver archiver;
    private final FileServiceOrchestrator fileServiceOrchestrator;

    @Autowired
    public FileController(FileMetaDataService fileMetaDataService, JWTService jwtService, UserRepo userRepo, Archiver archiver, FileServiceOrchestrator fileServiceOrchestrator) {
        this.fileMetaDataService = fileMetaDataService;
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        this.archiver = archiver;
        this.fileServiceOrchestrator = fileServiceOrchestrator;
    }

    @PostMapping({"/upload"})
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader("Authentication") String authToken) {
        try {
            FileMetadata metadata = this.fileMetaDataService.uploadFileMetaData(file, getOwnerId(authToken));
            fileServiceOrchestrator.uploadFile(file);
            return ResponseEntity.ok(Map.ofEntries(Map.entry("imageId", Long.toHexString(metadata.getId())), Map.entry("name", metadata.getName()), Map.entry("size", metadata.getSize())));
        } catch (IOException var4) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException var5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

//    @GetMapping({"/{fileId}"})
//    public String getFile(@PathVariable Long fileId, @RequestHeader("Authentication") String authToken) throws IOException {
//        FileMetadata metadata = this.fileMetaDataService.getFilesMetadata(fileId, getOwnerId(authToken));
//        return AwsImplementationFileService.getFile(AwsImplementationFileService.generateKey(metadata));
//    }

    /*
            filename=arhiva.zip
     */
    @GetMapping("/downloadAllFilesAsArchive/{type}")
    public ResponseEntity<byte[]> downloadAllFilesAsArchive(@RequestHeader("Authentication") String authToken, @PathVariable String type) throws Exception {
        if (archiver.isArchiveTypeAccepted(type)) {
            byte[] zipBytes = fileServiceOrchestrator.manageDownloadAllFilesAsArchive(getOwnerId(authToken));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", archiveName))
                    .contentType(new MediaType("application", "zip"))
                    .body(zipBytes);
        } else {
            throw new Exception("Unsupported Archive type");
        }
    }

    private Long getOwnerId(String authToken) {
        String jwtTokenValue = JwtHelper.getJwtTokenValue(authToken);
        String username = jwtService.extractUserName(jwtTokenValue);
        return userRepo.findByUserName(username).getId();
    }
}