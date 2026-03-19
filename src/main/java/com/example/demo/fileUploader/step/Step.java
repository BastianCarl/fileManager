package com.example.demo.fileUploader.step;

import com.example.demo.model.FileProcessingStep;
import com.example.demo.model.Resource;
import org.apache.commons.lang3.tuple.Pair;

public interface Step {
    FileProcessingStep process(Resource resource, FileProcessingStep previousFileProcessingStep);

    FileProcessingStep nextState();

    default boolean shouldProcess(FileProcessingStep previousState) {
        return (previousState.getOrder() <= nextState().getOrder());
    }
}