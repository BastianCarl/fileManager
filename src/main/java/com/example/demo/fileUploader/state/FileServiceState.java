package com.example.demo.fileUploader.state;

import com.example.demo.files.FileService;
import com.example.demo.model.AuditState;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import static com.example.demo.model.AuditState.*;

@Component
public class FileServiceState implements State {
    private final DiskState diskState;
    private final FileService fileService;
    private final AuditService auditService;

    @Autowired
    public FileServiceState(@Lazy AuditService auditService,
                            @Lazy FileService fileService,
                            DiskState diskState)
    {
        this.auditService = auditService;
        this.diskState = diskState;
        this.fileService = fileService;
    }

    @Override
    public State process(Resource resource) {
        auditService.updateOrCreate(resource.getFileMetadata(), FILE_SERVICE_STARTED);
        AuditState previousState = auditService.getAuditState(resource.getFileMetadata().getCode());
        if (shouldProcess(previousState)){
            fileService.uploadFile(resource);
            auditService.updateOrCreate(resource.getFileMetadata(), nextState());
        }
        return diskState;
    }

    @Override
    public AuditState nextState() {
        return FILE_SERVICE_DONE;
    }
}