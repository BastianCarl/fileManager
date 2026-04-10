package com.example.demo.model.dto;

import com.example.demo.model.fileUploadingStep.Step;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProgressUpdate {
  private String uuid;
  private ProgressUpdateStatus status;
  private int progress;
  private String message;

  public enum ProgressUpdateStatus {
    IN_PROGRESS,
    DONE
  }

  public ProgressUpdate(
      String uuid,
      ProgressUpdateStatus progressUpdateStatus,
      int currentStep,
      int totalSteps,
      Step step) {
    this.uuid = uuid;
    this.status = progressUpdateStatus;
    this.progress = (currentStep * 100) / totalSteps;
    this.message = "Running step: " + step.getClass().getSimpleName();
  }
}
