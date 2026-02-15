package com.example.demo.fileState;

import com.example.demo.model.AuditState;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DoneState extends AuditStateState {
    @Autowired
    public DoneState(AuditService auditService) {
        super(auditService, AuditState.DONE);
    }

    @Override
    public AuditStateState process(Resource resource) {
        auditService.updateOrCreate(resource.getFileMetadata().getCode(), this.auditState);
        return null;
    }
}