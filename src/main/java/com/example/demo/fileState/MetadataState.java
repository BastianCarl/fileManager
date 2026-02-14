package com.example.demo.fileState;

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
    private final AuditState auditState;
    @Autowired
    public MetadataState(@Lazy AuditService auditService,
                         @Lazy FileMetaDataService fileMetaDataService,
                         FileServiceState fileServiceState)
    {
        super(auditService);
        this.fileServiceState = fileServiceState;
        this.fileMetaDataService = fileMetaDataService;
        this.auditState = AuditState.METADATA;
    }

    @Override
    public State process(Resource resource) {
        AuditState auditState = auditService.getAuditState(resource.getFileMetadata().getHashValue());
        if (shouldProcess(auditState,this.auditState)){
            fileMetaDataService.uploadFileMetaData(resource.getFileMetadata());
            auditService.updateOrCreate(resource.getFileMetadata().getCode(), this.auditState);
        }
       return fileServiceState;
    }
}