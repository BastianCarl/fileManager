package com.example.demo.fileUploader.state;

import com.example.demo.files.FileService;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class FileServiceState extends AuditState {
    private final DiskState diskState;
    private final FileService fileService;
    @Autowired
    public FileServiceState(@Lazy AuditService auditService,
                            @Lazy FileService fileService,
                            DiskState diskState)
    {
        super(auditService, com.example.demo.model.AuditState.FILE_SERVICE);
        this.diskState = diskState;
        this.fileService = fileService;
    }

    @Override
    public AuditState process(Resource resource) {
        com.example.demo.model.AuditState auditState = auditService.getAuditState(resource.getFileMetadata().getCode());
        if (shouldProcess(auditState, this.auditState)){
            fileService.uploadFile(resource);
            auditService.updateOrCreate(resource.getFileMetadata().getCode(), this.auditState);
        }
        return diskState;
    }
}