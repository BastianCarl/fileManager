package com.example.demo.repository;

import com.example.demo.model.FileAuditState;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AuditRepository extends JpaRepository<FileAuditState, Long> {
    Optional<FileAuditState> findByCode(String code);
}