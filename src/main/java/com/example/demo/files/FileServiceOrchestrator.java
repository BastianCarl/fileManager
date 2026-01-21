package com.example.demo.files;

import com.example.demo.model.FileMetadata;
import com.example.demo.repository.FileMetadataRepository;
import com.example.demo.service.UserService;
import com.example.demo.utility.Archiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Component
public class FileServiceOrchestrator {

    @Value("${limit.for.downloading}")
    private int LIMIT_FOR_DOWNLOADING;
    private final FileService fileServiceImplementation;
    private final FileMetadataRepository fileMetadataRepository;
    private final UserService userService;
    private final Archiver archiver;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 5;

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
        if (files == null || files.isEmpty()) {
            return Collections.emptyMap();
        }

        ThreadManagerContext threadContext = new ThreadManagerContext();
        ExecutorService executor = Executors.newFixedThreadPool(DOWNLOAD_THREAD_POOL_SIZE);
        List<Future<?>> futures = new ArrayList<>();

        long accumulatedSize = 0;

        for (FileMetadata fileMetadata : files) {
            long fileSize = fileMetadata.getSize();

            if (accumulatedSize + fileSize > LIMIT_FOR_DOWNLOADING) {
                break;
            }

            accumulatedSize += fileSize;

            Future<?> future = executor.submit(() -> {
                byte[] fileContent = downloadFileWithRetry(fileMetadata);
                if (fileContent != null) {
                    threadContext.addElement(fileMetadata.getName(), fileContent);
                }
            });

            futures.add(future);
        }

        awaitCompletion(futures);
        executor.shutdown();
        return threadContext.getFilesMap();
    }

    private byte[] downloadFileWithRetry(FileMetadata fileMetadata) {
        try {
            return fileServiceImplementation.downloadFile(fileMetadata);
        } catch (IOException e) {
            try {
                return fileServiceImplementation.downloadFile(fileMetadata);
            } catch (IOException ex) {
                System.err.println("Failed to download file after retry: " + fileMetadata.getName());
                ex.printStackTrace();
                return null;
            }
        }
    }

    private void awaitCompletion(List<Future<?>> futures) {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Download thread was interrupted");
            } catch (ExecutionException e) {
                System.err.println("Error during file download");
                e.getCause().printStackTrace();
            }
        }
    }

    @Cacheable(value ="downloadCache", key = "#fileId")
    public byte[] manageDownloadFile(Long ownerId, Long fileId) throws IOException {
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
        }
    }
}
