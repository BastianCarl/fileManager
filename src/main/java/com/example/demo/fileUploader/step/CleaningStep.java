package com.example.demo.fileUploader.step;

import com.example.demo.model.FileProcessingStep;
import com.example.demo.service.AuditService;
import com.example.demo.utility.FileHelper;
import com.example.demo.model.Resource;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.File;
import static com.example.demo.model.FileProcessingStep.*;

@Component
public class CleaningStep implements Step {
    private final FileHelper fileHelper;
    private final AuditService auditService;
    private final DoneStep doneStep;

    @Autowired
    public CleaningStep(FileHelper fileHelper, AuditService auditService, DoneStep doneStep) {
        this.fileHelper = fileHelper;
        this.auditService = auditService;
        this.doneStep = doneStep;
    }

    @Override
    public Pair<Step, FileProcessingStep> process(Resource resource, FileProcessingStep previousFileProcessingStep) {
        if (shouldProcess(previousFileProcessingStep)) {
            auditService.updateOrCreate(resource.getFileMetadata(), CLEANING_STARTED);
            File file = resource.getFile();
            try {
                fileHelper.deleteFile(file.toPath());
                auditService.updateOrCreate(resource.getFileMetadata(), nextState());
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
        return Pair.of(doneStep, nextState());
    }

    @Override
    public FileProcessingStep nextState() {
        return CLEANING_DONE;
    }
}