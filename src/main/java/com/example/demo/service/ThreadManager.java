package com.example.demo.service;

import com.example.demo.files.AwsImplementationFileService;
import com.example.demo.model.FileMetadata;
import com.example.demo.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import static com.example.demo.utility.Archiver.createZip;

@Component
public class ThreadManager {

    @Value("${limit.for.downloading}")
    private int LIMIT_FOR_DOWNLOADING;
    @Autowired
    private AwsImplementationFileService awsImplementationFileService;
    @Autowired
    private FileMetadataRepository repository;

    public void uploadFile(MultipartFile file) throws IOException {
        awsImplementationFileService.uploadFile(file);
    }

    private Map<String, byte[]> ManageDownloadAllFiles(List<FileMetadata> files) {
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
               byte[] file;
               try {
                   file = awsImplementationFileService.downloadFile(fileMetadata);
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }
               threadManagerContext.addElement(fileMetadata.getName(), file);
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
        return createZip(ManageDownloadAllFiles(files));
    }

    public static class ThreadManagerContext {
        private Map<String, byte[]> filesMap = new HashMap<>();
        public Map<String, byte[]> getFilesMap() {
            return filesMap;
        }
        synchronized public void addElement(String key, byte[] value) {
            filesMap.put(key, value);
        }
    }
}
