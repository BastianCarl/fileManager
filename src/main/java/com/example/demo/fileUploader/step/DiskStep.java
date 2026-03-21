package com.example.demo.fileUploader.step;

import static com.example.demo.model.FileProcessingStep.DISK_DONE;
import static com.example.demo.model.FileProcessingStep.DISK_STARTED;

import com.example.demo.model.FileProcessingStep;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import com.example.demo.utility.FileHelper;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@FileUploaderJobStep
@Order(4)
@Component
public class DiskStep implements Step {
  private final AuditService auditService;
  private final FileHelper fileHelper;

  @Value("#{T(java.nio.file.Paths).get('${file.uploader.job.backup.path}')}")
  private Path backupPath;

  @Value("${file.uploader.job.date.format}")
  private String DATE_FORMAT;

  private DateTimeFormatter formatter;

  @Autowired
  public DiskStep(@Lazy AuditService auditService, @Lazy FileHelper fileHelper, DoneStep doneStep) {
    this.auditService = auditService;
    this.fileHelper = fileHelper;
  }

  @PostConstruct
  public void init() {
    formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
  }

  @Override
  public FileProcessingStep process(
      Resource resource, FileProcessingStep currentFileProcessingStep) {
    if (shouldProcess(currentFileProcessingStep)) {
      currentFileProcessingStep = DISK_STARTED;
      auditService.upsert(resource.getFileMetadata(), DISK_STARTED);
      File file = resource.getFile();
      fileHelper.move(
          file.toPath(), Path.of(backupPath.toString(), LocalDate.now().format(formatter)));
      auditService.upsert(resource.getFileMetadata(), nextState());
      currentFileProcessingStep = DISK_DONE;
    }
    return currentFileProcessingStep;
  }

  @Override
  public FileProcessingStep nextState() {
    return DISK_DONE;
  }
}
