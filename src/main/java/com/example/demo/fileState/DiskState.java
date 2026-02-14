package com.example.demo.fileState;

import com.example.demo.FileHelper;
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
public final class DiskState extends State {
    private final FileHelper fileHelper;
    private final AuditState auditState;
    @Value("#{T(java.nio.file.Paths).get('${file.uploader.job.backup.path}')}")
    private Path backupPath;
    @Value("${file.uploader.job.date.format}")
    private String DATE_FORMAT;
    private DateTimeFormatter formatter;

    @Autowired
    public DiskState(@Lazy AuditService auditService, @Lazy FileHelper fileHelper)
    {
        super(auditService);
        this.fileHelper = fileHelper;
        auditState = AuditState.DISK;
    }

    @PostConstruct
    public void init() {
        formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    }

    @Override
    public State process(Resource resource) {
        AuditState auditState = auditService.getAuditState(resource.getFileMetadata().getHashValue());
        if (shouldProcess(auditState, this.auditState)){
            File file = (File) resource.getSource();
            fileHelper.move(file.toPath(), Path.of(backupPath.toString(), LocalDate.now().format(formatter)));
            auditService.updateOrCreate(resource.getFileMetadata().getCode(), this.auditState);
        }
        return null;
    }
}