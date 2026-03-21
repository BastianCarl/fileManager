package com.example.demo.model;

import lombok.Getter;

@Getter
public enum FileProcessingStep {
  NOT_FOUND(0),
  METADATA_STARTED(2),
  METADATA_DONE(3),
  FILE_SERVICE_STARTED(4),
  FILE_SERVICE_DONE(5),
  DISK_STARTED(6),
  DISK_DONE(7),
  DONE(10);
  final int order;

  FileProcessingStep(int order) {
    this.order = order;
  }
}
