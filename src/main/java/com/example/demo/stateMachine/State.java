package com.example.demo.stateMachine;

import com.example.demo.fileUploader.FileUploaderService;
import com.example.demo.model.AuditState;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;

public abstract class State  {
    protected final FileUploaderService fileUploaderService;
    protected final AuditService auditService;
    public State(FileUploaderService fileUploaderService, AuditService auditService) {
        this.fileUploaderService = fileUploaderService;
        this.auditService = auditService;
    }
    public abstract State process(Resource resource);

    public boolean shouldProcess(AuditState previousState, AuditState currentState) {
        return (previousState.getOrder() <= currentState.getOrder());
    }
}