package com.example.demo.fileUploader.step;

import com.example.demo.model.FileProcessingStep;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static com.example.demo.model.FileProcessingStep.DONE;

@Component
public class DoneStep implements Step {
    private final AuditService auditService;

    @Autowired
    public DoneStep(AuditService auditService) {
       this.auditService = auditService;
    }

    @Override
    public Pair<Step, FileProcessingStep> process(Resource resource, FileProcessingStep previousFileProcessingStep) {
        if (shouldProcess(previousFileProcessingStep)) {
            auditService.updateOrCreate(resource.getFileMetadata(), nextState());
        }
        return Pair.of(null, nextState());
    }

    @Override
    public FileProcessingStep nextState() {
        return DONE;
    }
}