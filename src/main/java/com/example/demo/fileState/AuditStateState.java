package com.example.demo.fileState;

import com.example.demo.model.AuditState;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;

public abstract class AuditStateState implements State {
    protected final AuditState auditState;
    protected final AuditService auditService;
    public AuditStateState(AuditService auditService, AuditState auditState) {
        this.auditService = auditService;
        this.auditState = auditState;
    }
    public abstract AuditStateState process(Resource resource);

    public boolean shouldProcess(AuditState previousState, AuditState currentState) {
        return (previousState.getOrder() <= currentState.getOrder());
    }
}