package com.example.demo.service;

import com.example.demo.model.Audit;
import com.example.demo.model.AuditState;
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

    public AuditState getAuditState(String hashValue) {
        return auditRepository.findByHashValue(hashValue)
                .map(Audit::getState)
                .orElse(AuditState.NOT_FOUND);
    }

    public Audit updateOrCreate(String hashValue, AuditState newState) {
        Audit audit = auditRepository.findByHashValue(hashValue)
                .orElseGet(() -> {
                    Audit a = new Audit();
                    a.setHashValue(hashValue);
                    return a;
                });

        audit.setState(newState);
        return auditRepository.save(audit);
    }
}