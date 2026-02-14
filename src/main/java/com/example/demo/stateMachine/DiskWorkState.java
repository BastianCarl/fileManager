package com.example.demo.stateMachine;

import com.example.demo.FileHelper;
import com.example.demo.fileUploader.FileUploaderService;
import com.example.demo.model.AuditState;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public final class DiskWorkState extends State{

    private final FileHelper fileHelper;
    @Value("#{T(java.nio.file.Paths).get('${file.uploader.job.backup.path}')}")
    private Path backupPath;
    @Value("${file.uploader.job.date.format}")
    private String DATE_FORMAT;
    private DateTimeFormatter formatter;

    @Autowired
    public DiskWorkState(@Lazy FileUploaderService fileUploaderService,
                         @Lazy AuditService auditService,
                         @Lazy FileHelper fileHelper)
    {
        super(fileUploaderService, auditService);
        this.fileHelper = fileHelper;
    }

    @PostConstruct
    public void init() {
        formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    }

    @Override
    public State process(Resource resource) {
        System.err.println("DiskWorkState process");
        AuditState auditState = auditService.getAuditState(resource.getFileMetadata().getHashValue());
        if (shouldProcess(auditState, AuditState.DISK_WORK)){
            File file = (File) resource.getSource();
            fileHelper.move(file.toPath(), Path.of(backupPath.toString(), LocalDate.now().format(formatter)));
            auditService.updateOrCreate(resource.getFileMetadata().getHashValue(), AuditState.DISK_WORK);
        }
        return null;
    }
}