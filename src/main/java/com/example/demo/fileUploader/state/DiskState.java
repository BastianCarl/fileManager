package com.example.demo.fileUploader.state;

import com.example.demo.model.AuditState;
import com.example.demo.utility.FileHelper;
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
import static com.example.demo.model.AuditState.DISK;

@Component
public class DiskState implements State {
    private final AuditService auditService;
    private final FileHelper fileHelper;
    private final DoneState doneState;
    @Value("#{T(java.nio.file.Paths).get('${file.uploader.job.backup.path}')}")
    private Path backupPath;
    @Value("${file.uploader.job.date.format}")
    private String DATE_FORMAT;
    private DateTimeFormatter formatter;

    @Autowired
    public DiskState(@Lazy AuditService auditService, @Lazy FileHelper fileHelper, DoneState doneState)
    {
        this.auditService = auditService;
        this.fileHelper = fileHelper;
        this.doneState = doneState;
    }

    @PostConstruct
    public void init() {
        formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    }

    @Override
    public State process(Resource resource) {
        AuditState previousState = auditService.getAuditState(resource.getFileMetadata().getCode());
        if (shouldProcess(previousState)){
            File file = resource.getFile();
            fileHelper.move(file.toPath(), Path.of(backupPath.toString(), LocalDate.now().format(formatter)));
            auditService.updateOrCreate(resource.getFileMetadata(), nextState());
        }
        return doneState;
    }

    @Override
    public AuditState nextState() {
        return DISK;
    }
}