package com.example.demo.stateMachine;

import com.example.demo.fileUploader.FileUploaderService;
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
public final class ExternalProvider extends State {

    private final DiskWorkState diskWorkState;
    private final FileService fileService;

    @Autowired
    public ExternalProvider(@Lazy FileUploaderService fileUploaderService,
                            @Lazy AuditService auditService,
                            @Lazy FileService fileService,
                            DiskWorkState diskWorkState)
    {
        super(fileUploaderService, auditService);
        this.diskWorkState = diskWorkState;
        this.fileService = fileService;
    }

    @Override
    public State process(Resource resource) {
        System.err.println("EXTERNAL PROVIDER");
        AuditState auditState =  auditService.getAuditState(resource.getFileMetadata().getHashValue());
        if (shouldProcess(auditState, AuditState.EXTERNAL_PROVIDE)){
            switch (resource.getSource()) {
                case File file -> fileService.uploadFile(file);
                case MultipartFile multipartFile -> fileService.uploadFile(multipartFile);
                default -> throw new RuntimeException("Unsupported file type: " + resource.getSource());
            }
            auditService.updateOrCreate(resource.getFileMetadata().getHashValue(), AuditState.EXTERNAL_PROVIDE);
        }
        return diskWorkState;
    }
}