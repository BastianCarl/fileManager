package com.example.demo.fileUploader;

import com.example.demo.FileHelper;
import com.example.demo.files.FileServiceOrchestrator;
import com.example.demo.model.FileMetadata;
import com.example.demo.model.FileMetadataMapper;
import com.example.demo.service.FileMetaDataService;
import com.example.demo.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.example.demo.service.UserService.userDTO;
@Service
public class FileUploaderService {


    private final FileServiceOrchestrator fileServiceOrchestrator;
    private final UserService userService;
    private final FileMetaDataService fileMetaDataService;
    private final FileHelper fileHelper;
    private final FileMetadataMapper fileMetadataMapper;
    @Value("${file.uploader.job.date.format}")
    private String DATE_FORMAT;
    @Value("#{T(java.nio.file.Paths).get('${file.uploader.job.backup.path}')}")
    private Path backupPath;
    @Value("#{T(java.nio.file.Paths).get('${file.uploader.job.failed.path}')}")
    private Path failedPath;
    private DateTimeFormatter formatter;

    @Autowired
    public FileUploaderService(
            FileServiceOrchestrator fileServiceOrchestrator,
            UserService userService,
            FileMetaDataService fileMetaDataService,
            FileHelper fileHelper,
            FileMetadataMapper fileMetadataMapper
    ) {
        this.fileServiceOrchestrator = fileServiceOrchestrator;
        this.userService = userService;
        this.fileMetaDataService = fileMetaDataService;
        this.fileHelper = fileHelper;
        this.fileMetadataMapper = fileMetadataMapper;
    }

    @PostConstruct
    public void init() {
        formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        fileHelper.checkDirectory(backupPath);
        fileHelper.checkDirectory(failedPath);
    }

    @Retryable(retryFor = Exception.class)
    public void process(File file) {
        FileMetadata fileMetadata = fileMetadataMapper.map(file, userService.getOwnerId(userDTO));
        if (!fileMetaDataService.checkFileExists(fileMetadata)) {
            fileServiceOrchestrator.uploadFile(file, fileMetadata);
        }
        fileHelper.move(file, Path.of(backupPath.toString(), LocalDate.now().format(formatter)));
    }

    @Recover
    public void recover(Exception e, File file) throws IOException {
        fileHelper.move(file, Path.of(failedPath.toString(), LocalDate.now().format(formatter)));
    }
}