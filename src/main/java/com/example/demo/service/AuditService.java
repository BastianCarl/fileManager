package com.example.demo.service;

import com.example.demo.model.FileAuditState;
import com.example.demo.model.FileMetadata;
import com.example.demo.model.FileProcessingStep;
import com.example.demo.repository.AuditRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
  private final AuditRepository auditRepository;

  @Autowired
  public AuditService(AuditRepository auditRepository) {
    this.auditRepository = auditRepository;
  }

  public FileProcessingStep getAuditState(FileMetadata fileMetadata) {
    String code = fileMetadata.getCode();
    return auditRepository
        .findByCode(code)
        .map(FileAuditState::getStep)
        .orElse(FileProcessingStep.NOT_FOUND);
  }

  public FileAuditState updateOrCreate(FileMetadata fileMetadata, FileProcessingStep newState) {
    String code = fileMetadata.getCode();
    FileAuditState fileAuditState =
        auditRepository
            .findByCode(code)
            .orElseGet(
                () -> {
                  FileAuditState a = new FileAuditState();
                  a.setId(UUID.randomUUID());
                  a.setCode(code);
                  return a;
                });

    fileAuditState.setStep(newState);
    return auditRepository.save(fileAuditState);
  }

  public Optional<FileAuditState> findById(String id) {
    return auditRepository.findById(UUID.fromString(id));
  }
}
