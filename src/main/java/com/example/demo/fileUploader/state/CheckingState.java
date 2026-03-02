package com.example.demo.fileUploader.state;
import com.example.demo.model.AuditState;
import com.example.demo.model.Resource;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CheckingState implements State{
    private final MetadataState metadataState;
    private final CleaningState cleaningState;
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckingState.class);

    @Autowired
    public CheckingState(MetadataState metadataState, CleaningState cleaningState) {
        this.metadataState = metadataState;
        this.cleaningState = cleaningState;
    }

    @Override
    public Pair<State, AuditState> process(Resource resource, AuditState currentAuditState) {
       if (currentAuditState != AuditState.DONE) {
           return Pair.of(metadataState, currentAuditState);
       }else {
           LOGGER.info("Duplicated File: {}. Moving directly to backup", resource.getFileMetadata().getName());
           return Pair.of(cleaningState, currentAuditState);
       }
    }

    @Override
    public AuditState nextState() {
        return null;
    }
}