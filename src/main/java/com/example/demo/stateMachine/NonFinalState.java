package com.example.demo.stateMachine;

import com.example.demo.fileUploader.FileUploaderService;
import com.example.demo.service.AuditService;

public abstract class NonFinalState extends  State{

    public NonFinalState(FileUploaderService fileUploaderService, AuditService auditService) {
        super(fileUploaderService, auditService);
    }
}
