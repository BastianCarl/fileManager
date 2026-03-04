package com.example.demo.fileUploader.step;
import com.example.demo.model.FileProcessingStep;
import com.example.demo.model.Resource;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CheckingStep implements Step {
    private final MetadataStep metadataStep;
    private final CleaningStep cleaningStep;
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckingStep.class);

    @Autowired
    public CheckingStep(MetadataStep metadataStep, CleaningStep cleaningStep) {
        this.metadataStep = metadataStep;
        this.cleaningStep = cleaningStep;
    }

    @Override
    public Pair<Step, FileProcessingStep> process(Resource resource, FileProcessingStep currentFileProcessingStep) {
       if (currentFileProcessingStep != FileProcessingStep.DONE) {
           return Pair.of(metadataStep, currentFileProcessingStep);
       }else {
           LOGGER.info("Duplicated File: {}. Moving directly to backup", resource.getFileMetadata().getName());
           return Pair.of(cleaningStep, currentFileProcessingStep);
       }
    }

    @Override
    public FileProcessingStep nextState() {
        return null;
    }
}