package com.example.demo.fileUploader.state;

import com.example.demo.model.AuditState;
import com.example.demo.service.AuditService;
import com.example.demo.utility.FileHelper;
import com.example.demo.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.File;
import static com.example.demo.model.AuditState.*;

@Component
public class CleaningState implements State{
    private final FileHelper fileHelper;
    private final AuditService auditService;
    private final DoneState doneState;

    @Autowired
    public CleaningState(FileHelper fileHelper, AuditService auditService, DoneState doneState) {
        this.fileHelper = fileHelper;
        this.auditService = auditService;
        this.doneState = doneState;
    }

    @Override
    public State process(Resource resource) {
        auditService.updateOrCreate(resource.getFileMetadata(), CLEANING_STARTED);
        File file = resource.getFile();
        try {
            fileHelper.deleteFile(file.toPath());
            auditService.updateOrCreate(resource.getFileMetadata(), nextState());
            return doneState;
        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    @Override
    public AuditState nextState() {
        return CLEANING_DONE;
    }
}