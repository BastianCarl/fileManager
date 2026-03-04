package com.example.demo.fileUploader.step;

import com.example.demo.model.FileProcessingStep;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import com.example.demo.service.FileMetaDataService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import static com.example.demo.model.FileProcessingStep.*;

@Component
public class MetadataStep implements Step {
    private final FileServiceStep fileServiceStep;
    private final FileMetaDataService fileMetaDataService;
    private final AuditService auditService;

    @Autowired
    public MetadataStep(@Lazy AuditService auditService,
                        @Lazy FileMetaDataService fileMetaDataService,
                        FileServiceStep fileServiceStep)
    {
        this.auditService = auditService;
        this.fileServiceStep = fileServiceStep;
        this.fileMetaDataService = fileMetaDataService;
    }

    @Override
    public Pair<Step, FileProcessingStep> process(Resource resource, FileProcessingStep currentFileProcessingStep) {
        if (shouldProcess(currentFileProcessingStep)){
            auditService.updateOrCreate(resource.getFileMetadata(), METADATA_STARTED);
            fileMetaDataService.save(resource.getFileMetadata());
            auditService.updateOrCreate(resource.getFileMetadata(), nextState());
        }
       return Pair.of(fileServiceStep, nextState());
    }

    @Override
    public FileProcessingStep nextState() {
        return METADATA_DONE;
    }
}