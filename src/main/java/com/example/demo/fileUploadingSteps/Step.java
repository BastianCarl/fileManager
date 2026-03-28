package com.example.demo.fileUploadingSteps;

import com.example.demo.repository.model.FileProcessingStep;
import com.example.demo.repository.model.Resource;

import java.util.UUID;

public interface Step {
  FileProcessingStep process(
      Resource resource, FileProcessingStep previousFileProcessingStep, UUID uuid);

  FileProcessingStep nextState();

  default boolean shouldProcess(FileProcessingStep previousState) {
    return (previousState.getOrder() <= nextState().getOrder());
  }
}
