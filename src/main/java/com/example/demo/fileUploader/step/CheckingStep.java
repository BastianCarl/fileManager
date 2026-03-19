package com.example.demo.fileUploader.step;
import com.example.demo.model.FileProcessingStep;
import com.example.demo.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@UploaderJobStep
@Order(1)
@Component
public class CheckingStep implements Step {
    private final CleaningStep cleaningStep;
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckingStep.class);

    @Autowired
    public CheckingStep(CleaningStep cleaningStep) {
        this.cleaningStep = cleaningStep;
    }

    @Override
    public FileProcessingStep process(Resource resource, FileProcessingStep currentFileProcessingStep) {
       if (currentFileProcessingStep != FileProcessingStep.DONE) {
           return currentFileProcessingStep;
       }else {
           LOGGER.info("Duplicated File: {}. Moving directly to backup", resource.getFileMetadata().getName());
           return cleaningStep.process(resource, currentFileProcessingStep);
       }
    }

    @Override
    public FileProcessingStep nextState() {
        return null;
    }
}