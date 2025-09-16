package com.example.demo.controller;

import com.example.demo.aws.AwsClient;
import com.example.demo.model.FileMetadata;
import com.example.demo.repository.UserRepo;
import com.example.demo.service.FileService;
import com.example.demo.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/files"})
@AllArgsConstructor
public class FileController {

    private final FileService fileService;
    private final JWTService jwtService;
    private final UserRepo userRepo;

    @PostMapping({"/upload"})
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            FileMetadata metadata = this.fileService.uploadImage(file, getOwnerId(request));
            AwsClient.uploadFile(file);
            return ResponseEntity.ok(Map.ofEntries(Map.entry("imageId", Long.toHexString(metadata.getId())), Map.entry("name", metadata.getName()), Map.entry("size", metadata.getSize())));
        } catch (IOException var4) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException var5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping({"/{fileId}"})
    public String getFile(@PathVariable Long fileId, HttpServletRequest request) throws IOException {
        FileMetadata metadata = this.fileService.getImageMetadata(fileId, getOwnerId(request));
        return AwsClient.getFile(AwsClient.generateAwsKey(metadata));
    }

//    @GetMapping({"/all"})
//    public List<FileMetadata> getAllFiles(HttpServletRequest request) throws IOException {
//        return fileService.getImageMetadataList(getOwnerId(request));
//    }

    private Long getOwnerId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String jwtToken = authHeader.substring(7);
        String username = jwtService.extractUserName(jwtToken);
        return userRepo.findByUserName(username).getId();
    }

}
