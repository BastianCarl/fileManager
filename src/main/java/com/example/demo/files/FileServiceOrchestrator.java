package com.example.demo.files;

import com.example.demo.model.FileMetadata;
import com.example.demo.repository.FileMetadataRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.utility.Archiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class FileServiceOrchestrator {

    @Value("${limit.for.downloading}")
    private int LIMIT_FOR_DOWNLOADING;
    private final FileService fileServiceImplementation;
    private final FileMetadataRepository fileMetadataRepository;
    private final UserService userService;
    private final Archiver archiver;

    @Autowired
    public FileServiceOrchestrator(AwsImplementationFileService awsImplementationFileService,
                                   FileMetadataRepository fileMetadataRepository,
                                   UserService userService,
                                   Archiver archiver)
    {
        this.fileServiceImplementation = awsImplementationFileService;
        this.fileMetadataRepository = fileMetadataRepository;
        this.userService = userService;
        this.archiver = archiver;
    }

    public void uploadFile(MultipartFile file) throws IOException {
         fileServiceImplementation.uploadFile(file);
    }

    private Map<String, byte[]> manageDownloadAllFiles(List<FileMetadata> files) {
        final ThreadManagerContext threadManagerContext = new ThreadManagerContext();
        int totalDownloaded = 0;
        final var executor = Executors.newFixedThreadPool(5);
        List<Future<?>> futures = new ArrayList<>();
        for (FileMetadata fileMetadata : files) {
            if (totalDownloaded > LIMIT_FOR_DOWNLOADING) {
                break;
            }
           totalDownloaded += fileMetadata.getSize();
           futures.add(executor.submit(() -> {
               byte[] file = null;
               try {
                   file = fileServiceImplementation.downloadFile(fileMetadata);
               } catch (IOException e) {
                   try {
                       file = fileServiceImplementation.downloadFile(fileMetadata);
                   } catch (IOException ex) {
                       System.err.println("Could not download file");
                   }
               }
               if (file != null) {
                   threadManagerContext.addElement(fileMetadata.getName(), file);
               }
            }));
        }
        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return threadManagerContext.getFilesMap();
    }

    @Cacheable(value ="downloadCache", key = "#fileId")
    public byte[] manageDownloadFile(Long ownerId, Long fileId) throws IOException {
        try {
            Thread.sleep(5000);
        }catch (InterruptedException e) {}
        List<FileMetadata> files = userService.isAdmin(ownerId) ? fileMetadataRepository.findAll() : fileMetadataRepository.findByOwnerId(ownerId);
        Optional<FileMetadata> fileMetadata = files.stream().filter(file -> file.getId().equals(fileId)).findFirst();
        if (fileMetadata.isPresent()) {
            byte[] file = null;
            file = fileServiceImplementation.downloadFile(fileMetadata.get());
            return file;
        }
        return new byte[0];
    }

    public byte[] manageDownloadAllFilesAsArchive(Long ownerId) throws IOException {
        List<FileMetadata> files = userService.isAdmin(ownerId) ? fileMetadataRepository.findAll() : fileMetadataRepository.findByOwnerId(ownerId);
        return archiver.createZip(manageDownloadAllFiles(files));
    }

    @Cacheable(value ="searchCache", key = "#name")
    public String searchFile(String name) {
        try {
            Thread.sleep(5000);
        }catch (InterruptedException e) {}
        try {
        return fileMetadataRepository.findByName(name).getName();
        } catch (Exception e) {
            return "File not found";
        }
    }

    public static class ThreadManagerContext {
        private Map<String, byte[]> filesMap = new ConcurrentHashMap<>();
        public Map<String, byte[]> getFilesMap() {
            return filesMap;
        }
        synchronized public void addElement(String key, byte[] value) {
            filesMap.put(key, value);

    }}}
