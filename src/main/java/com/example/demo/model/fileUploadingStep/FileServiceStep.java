package com.example.demo.model.fileUploadingStep;

import static com.example.demo.model.FileProcessingStep.FILE_SERVICE_DONE;
import static com.example.demo.model.FileProcessingStep.FILE_SERVICE_STARTED;

import com.example.demo.files.FileService;
import com.example.demo.model.FileProcessingStep;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@FileUploaderJobStep
@UserUploadStep
@Component
@Order(3)
public class FileServiceStep implements Step {
  private final FileService fileService;
  private final AuditService auditService;

  @Autowired
  public FileServiceStep(@Lazy AuditService auditService, @Lazy FileService fileService) {
    this.auditService = auditService;
    this.fileService = fileService;
  }

  @Override
  public FileProcessingStep process(
      Resource resource, FileProcessingStep currentFileProcessingStep, UUID id) {
    if (shouldProcess(currentFileProcessingStep)) {
      auditService.upsert(resource.getFileMetadata(), FILE_SERVICE_STARTED, id);
      fileService.uploadFile(resource);
      auditService.upsert(resource.getFileMetadata(), nextState(), id);
      currentFileProcessingStep = nextState();
    }
    return currentFileProcessingStep;
  }

  @Override
  public FileProcessingStep nextState() {
    return FILE_SERVICE_DONE;
  }
}
