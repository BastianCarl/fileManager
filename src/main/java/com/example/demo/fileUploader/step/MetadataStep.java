package com.example.demo.fileUploader.step;

import static com.example.demo.model.FileProcessingStep.*;

import com.example.demo.model.FileProcessingStep;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import com.example.demo.service.FileMetaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@FileUploaderJobStep
@UserUploadStep
@Order(2)
@Component
public class MetadataStep implements Step {
  private final FileMetaDataService fileMetaDataService;
  private final AuditService auditService;

  @Autowired
  public MetadataStep(
      @Lazy AuditService auditService, @Lazy FileMetaDataService fileMetaDataService) {
    this.auditService = auditService;
    this.fileMetaDataService = fileMetaDataService;
  }

  @Override
  public FileProcessingStep process(
      Resource resource, FileProcessingStep currentFileProcessingStep) {
    if (shouldProcess(currentFileProcessingStep)) {
      currentFileProcessingStep = METADATA_STARTED;
      auditService.updateOrCreate(resource.getFileMetadata(), METADATA_STARTED);
      fileMetaDataService.save(resource.getFileMetadata());
      auditService.updateOrCreate(resource.getFileMetadata(), nextState());
      currentFileProcessingStep = nextState();
    }
    return currentFileProcessingStep;
  }

  @Override
  public FileProcessingStep nextState() {
    return METADATA_DONE;
  }
}
