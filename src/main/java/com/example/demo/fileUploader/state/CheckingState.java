package com.example.demo.fileUploader.state;
import com.example.demo.model.AuditState;
import com.example.demo.model.Resource;
import com.example.demo.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import static com.example.demo.model.AuditState.CHECKING;

@Component
public class CheckingState implements State{
    protected final AuditService auditService;
    private final MetadataState metadataState;
    private final CleaningState cleaningState;
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckingState.class);

    @Autowired
    public CheckingState(@Lazy AuditService auditService, MetadataState metadataState, CleaningState cleaningState) {
        this.auditService = auditService;
        this.metadataState = metadataState;
        this.cleaningState = cleaningState;
    }

    @Override
    public State process(Resource resource) {
       AuditState previousState = auditService.getAuditState(resource.getFileMetadata().getCode());
       if (previousState != AuditState.DONE) {
           return metadataState;
       }else {
           LOGGER.info("Duplicated File: {}. Moving directly to backup", resource.getFileMetadata().getName());
           return cleaningState;
       }
    }

    @Override
    public AuditState nextState() {
        return CHECKING;
    }
}