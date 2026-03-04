package com.example.demo.fileUploader.step;

import com.example.demo.files.FileService;
import com.example.demo.model.FileProcessingStep;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import static com.example.demo.model.FileProcessingStep.*;

@Component
public class FileServiceStep implements Step {
    private final DiskStep diskStep;
    private final FileService fileService;
    private final AuditService auditService;

    @Autowired
    public FileServiceStep(@Lazy AuditService auditService,
                           @Lazy FileService fileService,
                           DiskStep diskStep)
    {
        this.auditService = auditService;
        this.diskStep = diskStep;
        this.fileService = fileService;
    }

    @Override
    public Pair<Step, FileProcessingStep> process(Resource resource, FileProcessingStep currentFileProcessingStep) {
        if (shouldProcess(currentFileProcessingStep)){
            auditService.updateOrCreate(resource.getFileMetadata(), FILE_SERVICE_STARTED);
            fileService.uploadFile(resource);
            auditService.updateOrCreate(resource.getFileMetadata(), nextState());
        }
        return Pair.of(diskStep, nextState());
    }

    @Override
    public FileProcessingStep nextState() {
        return FILE_SERVICE_DONE;
    }
}