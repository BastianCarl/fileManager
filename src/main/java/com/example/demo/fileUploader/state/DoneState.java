package com.example.demo.fileUploader.state;

import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DoneState extends AuditState {
    @Autowired
    public DoneState(AuditService auditService) {
        super(auditService, com.example.demo.model.AuditState.DONE);
    }

    @Override
    public AuditState process(Resource resource) {
        auditService.updateOrCreate(resource.getFileMetadata().getCode(), this.auditState);
        return null;
    }
}