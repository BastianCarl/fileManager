package com.example.demo.fileUploader.state;

import com.example.demo.model.AuditState;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static com.example.demo.model.AuditState.DONE;

@Component
public class DoneState implements State {
    private final AuditService auditService;

    @Autowired
    public DoneState(AuditService auditService) {
       this.auditService = auditService;
    }

    @Override
    public State process(Resource resource) {
        AuditState previousState = auditService.getAuditState(resource.getFileMetadata());
        if (shouldProcess(previousState)) {
            auditService.updateOrCreate(resource.getFileMetadata(), nextState());
        }
        return null;
    }

    @Override
    public AuditState nextState() {
        return DONE;
    }
}