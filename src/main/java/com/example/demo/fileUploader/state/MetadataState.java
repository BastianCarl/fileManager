package com.example.demo.fileUploader.state;

import com.example.demo.model.AuditState;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import com.example.demo.service.FileMetaDataService;
import org.apache.commons.lang3.tuple.Pair;
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
    public Pair<State, AuditState> process(Resource resource, AuditState currentAuditState) {
        if (shouldProcess(currentAuditState)){
            auditService.updateOrCreate(resource.getFileMetadata(), METADATA_STARTED);
            fileMetaDataService.save(resource.getFileMetadata());
            auditService.updateOrCreate(resource.getFileMetadata(), nextState());
        }
       return Pair.of(fileServiceState, nextState());
    }

    @Override
    public AuditState nextState() {
        return METADATA_DONE;
    }
}