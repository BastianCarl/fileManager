package com.example.demo.fileUploader.state;
import com.example.demo.model.AuditState;
import com.example.demo.model.Resource;
import org.apache.commons.lang3.tuple.Pair;

public interface State {
    Pair<State, AuditState> process(Resource resource, AuditState previousAuditState);
    AuditState nextState();
    default boolean shouldProcess(AuditState previousState) {
        return (previousState.getOrder() <= nextState().getOrder());
    }
}