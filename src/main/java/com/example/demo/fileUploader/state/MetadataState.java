package com.example.demo.fileUploader.state;

import com.example.demo.model.AuditState;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import com.example.demo.service.FileMetaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import static com.example.demo.model.AuditState.*;

@Component
public class MetadataState implements State{
    private final FileServiceState fileServiceState;
    private final FileMetaDataService fileMetaDataService;
    private final AuditService auditService;

    @Autowired
    public MetadataState(@Lazy AuditService auditService,
                         @Lazy FileMetaDataService fileMetaDataService,
                         FileServiceState fileServiceState)
    {
        this.auditService = auditService;
        this.fileServiceState = fileServiceState;
        this.fileMetaDataService = fileMetaDataService;
    }

    @Override
    public State process(Resource resource) {
        auditService.updateOrCreate(resource.getFileMetadata(), METADATA_STARTED);
        AuditState previousState = auditService.getAuditState(resource.getFileMetadata().getCode());
        if (shouldProcess(previousState)){
            fileMetaDataService.save(resource.getFileMetadata());
            auditService.updateOrCreate(resource.getFileMetadata(), nextState());
        }
       return fileServiceState;
    }

    @Override
    public AuditState nextState() {
        return METADATA_DONE;
    }
}