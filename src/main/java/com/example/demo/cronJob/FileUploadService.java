package com.example.demo.cronJob;

import com.example.demo.FileUtils;
import com.example.demo.exception.DuplicatedFile;
import com.example.demo.files.FileServiceOrchestrator;
import com.example.demo.service.UserService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static com.example.demo.service.UserService.userDTO;

@Service
public class FileUploadService {

    private final FileServiceOrchestrator fileServiceOrchestrator;
    private final UserService userService;
    @Value("${cron.job.backup.path}")
    private String BACKUP_PATH;
    @Value("${cron.job.failed.path}")
    private String FAILED_PATH;
    @Value("${cron.job.pending.path}")
    private String PENDING_PATH;
    @Value("${cron.job.date.format}")
    private String DATE_FORMAT;
    private DateTimeFormatter formatter;


    private static Logger LOGGER = LoggerFactory.getLogger(FileUploadService.class);
    @Autowired
    public FileUploadService(FileServiceOrchestrator fileServiceOrchestrator, UserService userService) {
        this.fileServiceOrchestrator = fileServiceOrchestrator;
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        this.formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    }


    public void manageFilesProcessing() {
        File directory = new File(PENDING_PATH);
        var files = directory.listFiles(File::isFile);
        if (files != null) {
            for (File file : files) {
                this.manageFileProcessing(file);
            }
        }
    }

    public void manageFileProcessing(File file) {
        try {
            processFile(file);
            return;
        } catch (DuplicatedFile e) {
            LOGGER.info(e.getMessage());
            return;
        } catch (IOException e) {
            LOGGER.warn("IO error, retrying file: {}", file.getName());
        }

        try {
            processFile(file);
        } catch (DuplicatedFile e) {
            LOGGER.info(e.getMessage());
        } catch (IOException e) {
            moveToFailed(file);
        }
    }

    private void processFile(File file) throws IOException, DuplicatedFile {
        fileServiceOrchestrator.uploadFile(file, userService.getOwnerId(userDTO));
        FileUtils.move(file, Path.of(BACKUP_PATH, LocalDate.now().format(formatter)));
        LOGGER.info("File {} successfully uploaded", file.getName());
    }

    private void moveToFailed(File file) {
        LOGGER.error("Move to Failed file: {}", file.getName());
        try {
            FileUtils.move(file, Path.of(FAILED_PATH, LocalDate.now().format(formatter)));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}