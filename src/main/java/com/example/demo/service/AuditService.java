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

    // 1. Caută în DB
    Optional<FileAuditState> existing = auditRepository.findByCode(code);

    if (existing.isPresent()) {
      // 👉 UPDATE (entity existent)
      FileAuditState state = existing.get();
      state.setStep(newState);
      return auditRepository.save(state); // UPDATE sigur
    }

    // 👉 CREATE (entity nou)
    FileAuditState state = new FileAuditState();
    state.setId(UUID.randomUUID());
    state.setCode(code);
    state.setStep(newState);

    return auditRepository.save(state); // INSERT
  }

  public Optional<FileAuditState> findById(String id) {
    return auditRepository.findById(UUID.fromString(id));
  }
}
