package com.example.demo.controller;

import com.example.demo.files.FileServiceOrchestrator;
import com.example.demo.model.Version;
import com.example.demo.service.UserService;
import com.example.demo.utility.FileHelper;
import com.example.demo.utility.UriBuilderService;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
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
  private final FileHelper fileHelper;
  private final UriBuilderService uriBuilderService;

  @PostMapping
  public ResponseEntity<String> uploadFile(
      @RequestParam MultipartFile file, @RequestHeader("Authentication") String authToken)
      throws IOException {

    File tempFile = fileHelper.createTempFile(file);
    UUID uuid = fileServiceOrchestrator.upload(tempFile, authToken);
    String location = uriBuilderService.buildFileLocation(uuid);
    return ResponseEntity.accepted().header(HttpHeaders.LOCATION, location).body(uuid.toString());
  }

  @GetMapping
  public String search(@RequestParam(value = "name") String name) {
    return fileServiceOrchestrator.searchFile(name);
  }

  @GetMapping(params = {"type", "version"})
  public ResponseEntity<byte[]> getAllFiles(
      @RequestHeader("Authentication") String authHeader,
      @RequestParam String type,
      @RequestParam Version version) {
    return fileServiceOrchestrator
        .manageDownloadAllFilesAsArchive(type, version)
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

  @PostMapping("/backups/{date}/restorations")
  public void restoreBackup(
      @PathVariable("date") String date, @RequestHeader("Authentication") String authToken) {
    fileServiceOrchestrator.restoreBackup(date, userService.getOwnerId(authToken));
  }
}
