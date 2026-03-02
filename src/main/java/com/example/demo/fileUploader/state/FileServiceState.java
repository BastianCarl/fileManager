package com.example.demo.fileUploader.state;

import com.example.demo.files.FileService;
import com.example.demo.model.AuditState;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import org.apache.commons.lang3.tuple.Pair;
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
    public Pair<State, AuditState> process(Resource resource, AuditState currentAuditState) {
        if (shouldProcess(currentAuditState)){
            auditService.updateOrCreate(resource.getFileMetadata(), FILE_SERVICE_STARTED);
            fileService.uploadFile(resource);
            auditService.updateOrCreate(resource.getFileMetadata(), nextState());
        }
        return Pair.of(diskState, nextState());
    }

    @Override
    public AuditState nextState() {
        return FILE_SERVICE_DONE;
    }
}