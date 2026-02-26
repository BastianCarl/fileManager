package com.example.demo.service;

import com.example.demo.model.Audit;
import com.example.demo.model.AuditState;
import com.example.demo.model.FileMetadata;
import com.example.demo.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private final AuditRepository auditRepository;
    @Autowired
    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public AuditState getAuditState(String code) {
        return auditRepository.findByCode(code)
                .map(Audit::getState)
                .orElse(AuditState.NOT_FOUND);
    }

    public Audit updateOrCreate(FileMetadata fileMetadata, AuditState newState) {
        String code  = fileMetadata.getCode();
        Audit audit = auditRepository.findByCode(code)
                .orElseGet(() -> {
                    Audit a = new Audit();
                    a.setCode(code);
                    return a;
                });

        audit.setState(newState);
        return auditRepository.save(audit);
    }
}