package com.example.demo.repository;

import com.example.demo.model.FileAuditState;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<FileAuditState, Long> {
  Optional<FileAuditState> findByCode(String code);
}
