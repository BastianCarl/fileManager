package com.example.demo.fileUploader;

import com.example.demo.utility.FileHelper;
import com.example.demo.exception.DatabaseFailure;
import com.example.demo.exception.FileServiceFailure;
import com.example.demo.fileUploader.state.CheckingState;
import com.example.demo.fileUploader.state.State;
import com.example.demo.model.FileMetadataMapper;
import com.example.demo.model.Resource;
import com.example.demo.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.example.demo.service.UserService.userDTO;

@Service
public class FileUploaderService {
    private State initialOperationalState;
    private final UserService userService;
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
            UserService userService,
            FileHelper fileHelper,
            FileMetadataMapper fileMetadataMapper,
            CheckingState checkingState
    ) {
        this.userService = userService;
        this.fileHelper = fileHelper;
        this.fileMetadataMapper = fileMetadataMapper;
        this.initialOperationalState = checkingState;
    }

    @PostConstruct
    public void init() {
        formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        fileHelper.checkDirectory(backupPath);
        fileHelper.checkDirectory(failedPath);
    }

    @Retryable(retryFor = {DatabaseFailure.class, FileServiceFailure.class})
    public void process(File file) {
        State state = initialOperationalState;
        Resource resource = new Resource(file, fileMetadataMapper.map(file, userService.getOwnerId(userDTO)));
        while (state != null) {
            state = state.process(resource);
        }
    }

    @Recover
    public void recover(Exception e, File file){
        fileHelper.moveWithRenaming(file.toPath(), Path.of(failedPath.toString()), LocalDate.now().format(formatter) + "-" + file.getName());
    }
}