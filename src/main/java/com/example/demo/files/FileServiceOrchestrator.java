package com.example.demo.files;

import com.example.demo.FileUtils;
import com.example.demo.model.FileMetadata;
import com.example.demo.model.FileMetadataFactory;
import com.example.demo.repository.FileMetadataRepository;
import com.example.demo.service.FileMetaDataService;
import com.example.demo.service.UserService;
import com.example.demo.utility.Archiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

@Component
public class FileServiceOrchestrator {

    @Value("${limit.for.downloading}")
    private int LIMIT_FOR_DOWNLOADING;
    @Value("${cron.job.backup.path}")
    private String BACKUP_PATH;
    private final FileService fileService;
    private final FileMetadataRepository fileMetadataRepository;
    private final UserService userService;
    private final Archiver archiver;
    private final FileMetadataFactory fileMetadataFactory;
    private final FileMetaDataService fileMetaDataService;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 5;

    @Autowired
    public FileServiceOrchestrator(AwsImplementationFileService awsImplementationFileService,
                                   FileMetadataRepository fileMetadataRepository,
                                   UserService userService,
                                   FileMetaDataService fileMetaDataService,
                                   Archiver archiver,
                                   FileMetadataFactory fileMetadataFactory)
    {
        this.fileService = awsImplementationFileService;
        this.fileMetadataRepository = fileMetadataRepository;
        this.userService = userService;
        this.archiver = archiver;
        this.fileMetaDataService = fileMetaDataService;
        this.fileMetadataFactory = fileMetadataFactory;
    }

    public FileMetadata uploadFile(MultipartFile file, Long ownerId) throws IOException {
        fileService.uploadFile(file);
        return this.fileMetaDataService.uploadFileMetaData(fileMetadataFactory.create(file, ownerId));
    }

    public FileMetadata uploadFile(File file, Long ownerId){
        fileService.uploadFile(file);
        return this.fileMetaDataService.uploadFileMetaData(fileMetadataFactory.create(file, ownerId));
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
            return fileService.downloadFile(fileMetadata);
        } catch (IOException e) {
            try {
                return fileService.downloadFile(fileMetadata);
            } catch (IOException ex) {
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
            } catch (ExecutionException e) {
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
            file = fileService.downloadFile(fileMetadata.get());
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
        } catch (InterruptedException e) {}
        try {
        return fileMetadataRepository.findByName(name).getName();
        } catch (Exception e) {
            return "File not found";
        }
    }

    public void restoreFolder(List<MultipartFile> files, String date) {
        Path backupDirectoryWithDate = Path.of(BACKUP_PATH, date);
        FileUtils.deleteFilesOnly(backupDirectoryWithDate);
        try {
            FileUtils.createFiles(files,backupDirectoryWithDate);
        } catch (IOException e) {
            throw new RuntimeException(e);
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