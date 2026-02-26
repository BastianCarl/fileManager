package com.example.demo.fileUploader.state;
import com.example.demo.model.AuditState;
import com.example.demo.model.Resource;

public interface State {
    State process(Resource resource);
    AuditState nextState();
    default boolean shouldProcess(AuditState previousState) {
        return (previousState.getOrder() <= nextState().getOrder());
    }
}