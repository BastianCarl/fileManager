package com.example.demo.files;

import com.example.demo.model.FileMetadata;
import com.example.demo.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import static com.example.demo.utility.Archiver.createZip;

@Component
public class FileServiceOrchestrator {

    @Value("${limit.for.downloading}")
    private int LIMIT_FOR_DOWNLOADING;
    private final FileService fileServiceImplementation;
    private final FileMetadataRepository repository;

    @Autowired
    public FileServiceOrchestrator(AwsImplementationFileService awsImplementationFileService, FileMetadataRepository repository) {
        this.fileServiceImplementation = awsImplementationFileService;
        this.repository = repository;
    }
    @CachePut(value = "sebi", key = "#file.originalFilename")
    public String uploadFile(MultipartFile file) throws IOException {
         fileServiceImplementation.uploadFile(file);
         return file.getOriginalFilename();
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

    public byte[] manageDownloadAllFilesAsArchive(Long ownerId) throws IOException {
        List<FileMetadata> files = repository.findByOwnerId(ownerId);
        return createZip(manageDownloadAllFiles(files));
    }

    @Cacheable(value = "sebi")
    public String searchFile(String fileName) {
        try {
            Thread.sleep(5000);
        }catch (InterruptedException e) {}
        try {
        return repository.findByName(fileName).getName();
        } catch (Exception e) {
            return "File not found";
        }
    }

    public static class ThreadManagerContext {
        private Map<String, byte[]> filesMap = new HashMap<>();
        public Map<String, byte[]> getFilesMap() {
            return filesMap;
        }
        synchronized public void addElement(String key, byte[] value) {
            filesMap.put(key, value);

    }}}
