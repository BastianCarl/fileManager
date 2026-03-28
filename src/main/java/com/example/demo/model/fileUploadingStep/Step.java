package com.example.demo.model.fileUploadingStep;

import com.example.demo.model.FileProcessingStep;
import com.example.demo.model.Resource;

import java.util.UUID;

public interface Step {
  FileProcessingStep process(
      Resource resource, FileProcessingStep previousFileProcessingStep, UUID uuid);

  FileProcessingStep nextState();

  default boolean shouldProcess(FileProcessingStep previousState) {
    return (previousState.getOrder() <= nextState().getOrder());
  }
}
