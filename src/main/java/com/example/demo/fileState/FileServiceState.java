package com.example.demo.fileState;

import com.example.demo.files.FileService;
import com.example.demo.model.AuditState;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;

@Component
public final class FileServiceState extends State {
    private final DiskState diskState;
    private final FileService fileService;
    private final AuditState auditState;
    @Autowired
    public FileServiceState(@Lazy AuditService auditService,
                            @Lazy FileService fileService,
                            DiskState diskState)
    {
        super(auditService);
        this.diskState = diskState;
        this.fileService = fileService;
        this.auditState = AuditState.FILE_SERVICE;
    }

    @Override
    public State process(Resource resource) {
        AuditState auditState =  auditService.getAuditState(resource.getFileMetadata().getHashValue());
        if (shouldProcess(auditState, this.auditState)){
            switch (resource.getSource()) {
                case File file -> fileService.uploadFile(file);
                case MultipartFile multipartFile -> fileService.uploadFile(multipartFile);
                default -> throw new RuntimeException("Unsupported file type: " + resource.getSource());
            }
            auditService.updateOrCreate(resource.getFileMetadata().getHashValue(), this.auditState);
        }
        return diskState;
    }
}