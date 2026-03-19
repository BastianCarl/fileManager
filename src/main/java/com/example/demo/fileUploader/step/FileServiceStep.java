package com.example.demo.fileUploader.step;

import com.example.demo.files.FileService;
import com.example.demo.model.FileProcessingStep;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.example.demo.model.FileProcessingStep.*;

@FileUploaderFlowStep
@Component
@Order(3)
public class FileServiceStep implements Step {
    private final FileService fileService;
    private final AuditService auditService;

    @Autowired
    public FileServiceStep(@Lazy AuditService auditService,
                           @Lazy FileService fileService,
                           DiskStep diskStep
    ) {
        this.auditService = auditService;
        this.fileService = fileService;
    }

    @Override
    public FileProcessingStep process(Resource resource, FileProcessingStep currentFileProcessingStep) {
        if (shouldProcess(currentFileProcessingStep)) {
            auditService.updateOrCreate(resource.getFileMetadata(), FILE_SERVICE_STARTED);
            fileService.uploadFile(resource);
            auditService.updateOrCreate(resource.getFileMetadata(), nextState());
        }
        return nextState();
    }

    @Override
    public FileProcessingStep nextState() {
        return FILE_SERVICE_DONE;
    }
}