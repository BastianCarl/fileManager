package com.example.demo.stateMachine;

import com.example.demo.fileUploader.FileUploaderService;
import com.example.demo.model.AuditState;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import com.example.demo.service.FileMetaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
@Component
public final class MetadataState extends State {
    private final FileServiceState fileServiceState;
    private final FileMetaDataService fileMetaDataService;
    @Autowired
    public MetadataState(@Lazy FileUploaderService fileUploaderService,
                         @Lazy AuditService auditService,
                         FileServiceState fileServiceState,
                         FileMetaDataService fileMetaDataService)
    {
        super(fileUploaderService, auditService);
        this.fileServiceState = fileServiceState;
        this.fileMetaDataService = fileMetaDataService;
    }

    @Override
    public State process(Resource resource) {
        System.err.println("MetadataState process");
        AuditState auditState =  auditService.getAuditState(resource.getFileMetadata().getHashValue());
        if (shouldProcess(auditState, AuditState.METADATA)){
            fileMetaDataService.uploadFileMetaData(resource.getFileMetadata());
            auditService.updateOrCreate(resource.getFileMetadata().getHashValue(), AuditState.METADATA);
        }
       return fileServiceState;
    }
}