package com.example.demo.service;

import com.example.demo.model.FileProcessingStep;
import com.example.demo.model.Resource;
import com.example.demo.model.dto.ProgressUpdate;
import com.example.demo.model.fileUploadingStep.Step;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncFileUploadingService {

  private final ProgressSseService progressSseService;

  @Autowired
  public AsyncFileUploadingService(ProgressSseService progressSseService) {
    this.progressSseService = progressSseService;
  }

  @Async
  public void runAsyncSteps(
      Resource resource,
      FileProcessingStep fileProcessingStep,
      UUID id,
      int startIndex,
      List<Step> steps) {

    for (int i = startIndex; i < steps.size(); i++) {
      Step currentStep = steps.get(i);

      progressSseService.sendUpdate(
              id,
          new ProgressUpdate(
              ProgressUpdate.ProgressUpdateStatus.IN_PROGRESS, i, steps.size(), currentStep));

      fileProcessingStep = currentStep.process(resource, fileProcessingStep, id);
    }

    progressSseService.completeWithSuccess(id);
  }
}
