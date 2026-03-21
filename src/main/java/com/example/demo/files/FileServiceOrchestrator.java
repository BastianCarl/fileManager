package com.example.demo.files;

import com.example.demo.model.FileMetadata;
import com.example.demo.model.FileMetadataMapper;
import com.example.demo.model.Option;
import com.example.demo.model.Resource;
import com.example.demo.repository.FileMetadataRepository;
import com.example.demo.service.FileMetaDataService;
import com.example.demo.service.UserService;
import com.example.demo.utility.Archiver;
import com.example.demo.utility.FileHelper;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileServiceOrchestrator {
  @Value("${file.uploader.job.date.format}")
  private String DATE_FORMAT;

  @Value("${limit.for.downloading}")
  private int LIMIT_FOR_DOWNLOADING;

  @Value("${file.uploader.job.backup.path}")
  private String BACKUP_PATH;

  private final FileService fileService;
  private final FileMetadataRepository fileMetadataRepository;
  private final UserService userService;
  private final Archiver archiver;
  private final FileMetaDataService fileMetaDataService;
  private final FileHelper fileHelper;
  private final FileMetadataMapper fileMetadataMapper;
  private DateTimeFormatter formatter;
  private static final int DOWNLOAD_THREAD_POOL_SIZE = 5;

  @Autowired
  public FileServiceOrchestrator(
      AwsImplementationFileService awsImplementationFileService,
      FileMetadataRepository fileMetadataRepository,
      UserService userService,
      FileMetaDataService fileMetaDataService,
      Archiver archiver,
      FileMetadataMapper fileMetadataMapper,
      FileHelper fileHelper) {
    this.fileService = awsImplementationFileService;
    this.fileMetadataRepository = fileMetadataRepository;
    this.userService = userService;
    this.archiver = archiver;
    this.fileMetaDataService = fileMetaDataService;
    this.fileHelper = fileHelper;
    this.fileMetadataMapper = fileMetadataMapper;
  }

  @PostConstruct
  public void init() {
    formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
  }

  public Optional<FileMetadata> upload(MultipartFile file, String authToken) {
    try {
      if (fileMetaDataService.checkFileMetadataExists(
          fileHelper.sha256Hex(file.getInputStream()))) {
        return Optional.empty();
      }
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
    Long ownerId = userService.getOwnerId(authToken);
    FileMetadata fileMetadata = fileMetadataMapper.map(file, ownerId);
    fileService.uploadFile(new Resource(file, fileMetadata));
    FileMetadata saved = fileMetaDataService.save(fileMetadata);
    return Optional.of(saved);
  }

  public void deleteMetadata(FileMetadata fileMetadata) {
    this.fileMetaDataService.deleteFileMetaData(fileMetadata);
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

      Future<?> future =
          executor.submit(
              () -> {
                byte[] fileContent = downloadFileWithRetry(fileMetadata);
                if (fileContent != null) {
                  threadContext.addElement(
                      "v" + fileMetadata.getVersion() + fileMetadata.getName(), fileContent);
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
        throw new RuntimeException(ex);
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

  public Optional<byte[]> manageDownloadAllFilesAsArchive(String type, Option option) {
    if (archiver.isArchiveTypeAccepted(type)) {
      List<FileMetadata> files = option.getFiles(fileMetaDataService);
      try {
        return Optional.of(archiver.createZip(manageDownloadAllFiles(files)));
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return Optional.empty();
  }

  @Cacheable(value = "searchCache", key = "#name")
  public String searchFile(String name) {
    try {
      return fileMetadataRepository.findByName(name).getName();
    } catch (Exception e) {
      return "File not found";
    }
  }

  public void restoreBackup(String date, Long ownerId) {
    Path backup = Path.of(BACKUP_PATH, date);
    File[] files = fileHelper.listFiles(backup);
    for (File file : files) {
      FileMetadata fileMetadata = fileMetadataMapper.map(file, ownerId);
      fileMetaDataService.save(fileMetadata);
      fileService.uploadFile(new Resource(file, fileMetadata));
    }
  }

  public static class ThreadManagerContext {
    private Map<String, byte[]> filesMap = new ConcurrentHashMap<>();

    public Map<String, byte[]> getFilesMap() {
      return filesMap;
    }

    public synchronized void addElement(String key, byte[] value) {
      filesMap.put(key, value);
    }
  }
}
