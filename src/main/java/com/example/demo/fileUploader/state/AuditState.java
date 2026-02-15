package com.example.demo.fileUploader.state;

import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;

public abstract class AuditState implements State {
    protected final com.example.demo.model.AuditState auditState;
    protected final AuditService auditService;
    public AuditState(AuditService auditService, com.example.demo.model.AuditState auditState) {
        this.auditService = auditService;
        this.auditState = auditState;
    }
    public abstract AuditState process(Resource resource);

    public boolean shouldProcess(com.example.demo.model.AuditState previousState, com.example.demo.model.AuditState currentState) {
        return (previousState.getOrder() <= currentState.getOrder());
    }
}