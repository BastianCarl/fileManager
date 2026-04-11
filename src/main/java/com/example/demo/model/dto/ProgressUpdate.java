package com.example.demo.model.dto;

import com.example.demo.model.fileUploadingStep.Step;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProgressUpdate {
  private ProgressUpdateStatus status;
  private int progress;
  private String message;

  public enum ProgressUpdateStatus {
    STARTED,
    IN_PROGRESS,
    DONE
  }

  public ProgressUpdate(
      ProgressUpdateStatus progressUpdateStatus, int currentStep, int totalSteps, Step step) {
    this.status = progressUpdateStatus;
    this.progress = (currentStep * 100) / totalSteps;
    this.message = "Running step: " + step.getClass().getSimpleName();
  }

  public static ProgressUpdate createCompletedUpdate() {
    return new ProgressUpdate(ProgressUpdateStatus.DONE, 100, "Completed");
  }

  public static ProgressUpdate createStartedUpdate() {
    return new ProgressUpdate(ProgressUpdateStatus.STARTED, 0, "File processing started");
  }
}
