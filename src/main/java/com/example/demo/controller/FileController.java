package com.example.demo.controller;

import com.example.demo.files.FileServiceOrchestrator;
import com.example.demo.model.FileMetadata;
import com.example.demo.model.Option;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping({"/files"})
@RequiredArgsConstructor
public class FileController {

  @Value("${archive.name}")
  private String archiveName;

  private final FileServiceOrchestrator fileServiceOrchestrator;
  private final UserService userService;

  @PostMapping
  public ResponseEntity<FileMetadata> uploadFile(
      @RequestParam MultipartFile file, @RequestHeader("Authentication") String authToken) {
    return fileServiceOrchestrator
        .upload(file, authToken)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT).build());
  }

  @GetMapping
  public String search(@RequestParam(value = "file") String name) {
    return fileServiceOrchestrator.searchFile(name);
  }

  @GetMapping("/all/{type}")
  public ResponseEntity<byte[]> getAllFiles(
      @RequestHeader("Authentication") String authToken,
      @PathVariable String type,
      @RequestParam Option option) {
    return fileServiceOrchestrator
        .manageDownloadAllFilesAsArchive(type, option)
        .map(
            zipBytes ->
                ResponseEntity.ok()
                    .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(archiveName).build().toString())
                    .contentType(new MediaType("application", "zip"))
                    .body(zipBytes))
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @PostMapping("/restore")
  public void restoreBackup(
      @RequestParam("restoreDate") String date, @RequestHeader("Authentication") String authToken) {
    fileServiceOrchestrator.restoreBackup(date, userService.getOwnerId(authToken));
  }
}
