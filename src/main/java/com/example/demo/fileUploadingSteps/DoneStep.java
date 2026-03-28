package com.example.demo.fileUploadingSteps;

import com.example.demo.repository.model.FileProcessingStep;
import com.example.demo.repository.model.Resource;
import com.example.demo.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.example.demo.repository.model.FileProcessingStep.DONE;

@FileUploaderJobStep
@UserUploadStep
@Component
@Order(5)
public class DoneStep implements Step {
  private final AuditService auditService;

  @Autowired
  public DoneStep(AuditService auditService) {
    this.auditService = auditService;
  }

  @Override
  public FileProcessingStep process(
      Resource resource, FileProcessingStep previousFileProcessingStep, UUID uuid) {
    if (shouldProcess(previousFileProcessingStep)) {
      auditService.upsert(resource.getFileMetadata(), nextState(), uuid);
    }
    return nextState();
  }

  @Override
  public FileProcessingStep nextState() {
    return DONE;
  }
}
