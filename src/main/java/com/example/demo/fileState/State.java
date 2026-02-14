package com.example.demo.fileState;

import com.example.demo.model.AuditState;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;

public abstract class State  {
    protected final AuditService auditService;
    public State(AuditService auditService) {
        this.auditService = auditService;
    }
    public abstract State process(Resource resource);

    public boolean shouldProcess(AuditState previousState, AuditState currentState) {
        return (previousState.getOrder() <= currentState.getOrder());
    }
}