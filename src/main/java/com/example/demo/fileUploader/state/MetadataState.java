package com.example.demo.fileUploader.state;

import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import com.example.demo.service.FileMetaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
@Component
public class MetadataState extends AuditState {
    private final FileServiceState fileServiceState;
    private final FileMetaDataService fileMetaDataService;
    @Autowired
    public MetadataState(@Lazy AuditService auditService,
                         @Lazy FileMetaDataService fileMetaDataService,
                         FileServiceState fileServiceState)
    {
        super(auditService, com.example.demo.model.AuditState.METADATA);
        this.fileServiceState = fileServiceState;
        this.fileMetaDataService = fileMetaDataService;
    }

    @Override
    public AuditState process(Resource resource) {
        com.example.demo.model.AuditState auditState = auditService.getAuditState(resource.getFileMetadata().getCode());
        if (shouldProcess(auditState, this.auditState)){
            fileMetaDataService.uploadFileMetaData(resource.getFileMetadata());
            auditService.updateOrCreate(resource.getFileMetadata().getCode(), this.auditState);
        }
       return fileServiceState;
    }
}