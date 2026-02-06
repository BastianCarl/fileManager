package com.example.demo.cronJob;

import com.example.demo.FileUtils;
import com.example.demo.files.FileServiceOrchestrator;
import com.example.demo.model.FileMetadata;
import com.example.demo.service.FileMetaDataService;
import com.example.demo.service.UserService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static com.example.demo.service.UserService.userDTO;

@Service
public class FileUploadService {

    private final FileServiceOrchestrator fileServiceOrchestrator;
    private final UserService userService;
    private final FileMetaDataService fileMetaDataService;
    @Value("${cron.job.backup.path}")
    private String BACKUP_PATH;
    @Value("${cron.job.failed.path}")
    private String FAILED_PATH;
    @Value("${cron.job.pending.path}")
    private String PENDING_PATH;
    @Value("${cron.job.date.format}")
    private String DATE_FORMAT;
    private DateTimeFormatter formatter;
    private File directory;
    private static Logger LOGGER = LoggerFactory.getLogger(FileUploadService.class);
    @Autowired
    public FileUploadService(FileServiceOrchestrator fileServiceOrchestrator, UserService userService, FileMetaDataService fileMetaDataService) {
        this.fileServiceOrchestrator = fileServiceOrchestrator;
        this.userService = userService;
        this.fileMetaDataService = fileMetaDataService;
    }

    @PostConstruct
    public void init() {
        this.formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        directory = new File(PENDING_PATH);
    }


    public void manageFilesProcessing() {
        for (File file : FileUtils.listFiles(directory)) {
            if (fileMetaDataService.checkFileExists(new FileMetadata())) {
                fileServiceOrchestrator.uploadFile(file, userService.getOwnerId(userDTO));
                FileUtils.move(file, Path.of(BACKUP_PATH, LocalDate.now().format(formatter)));
                LOGGER.info("File {} successfully uploaded", file.getName());
            } else {
                FileUtils.move(file, Path.of(BACKUP_PATH, LocalDate.now().format(formatter)));
                LOGGER.info("Duplicated File  {} moving to backup without db update", file.getName());
            }
        }
    }

    private void moveToFailed(File file) {
        try {
            FileUtils.move(file, Path.of(FAILED_PATH, LocalDate.now().format(formatter)));
            LOGGER.error("Move to Failed file: {}", file.getName());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}