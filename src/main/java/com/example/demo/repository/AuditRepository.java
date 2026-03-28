package com.example.demo.repository;

import com.example.demo.repository.model.FileAuditState;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<FileAuditState, UUID> {
  Optional<FileAuditState> findByCode(String code);
}
