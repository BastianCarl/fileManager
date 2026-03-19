package com.example.demo.fileUploader.step;

import com.example.demo.model.FileProcessingStep;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.example.demo.model.FileProcessingStep.DONE;

@FileUploaderFlowStep
@Component
@Order(5)
public class DoneStep implements Step {
    private final AuditService auditService;

    @Autowired
    public DoneStep(AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public FileProcessingStep process(Resource resource, FileProcessingStep previousFileProcessingStep) {
        if (shouldProcess(previousFileProcessingStep)) {
            auditService.updateOrCreate(resource.getFileMetadata(), nextState());
        }
        return nextState();
    }

    @Override
    public FileProcessingStep nextState() {
        return DONE;
    }
}