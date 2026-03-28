package com.example.demo.model.fileUploadingStep;

import static com.example.demo.model.FileProcessingStep.DONE;

import com.example.demo.model.FileProcessingStep;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
