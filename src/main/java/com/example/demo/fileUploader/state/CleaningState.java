package com.example.demo.fileUploader.state;

import com.example.demo.model.AuditState;
import com.example.demo.utility.FileHelper;
import com.example.demo.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.File;
import static com.example.demo.model.AuditState.CLEANING;

@Component
public class CleaningState implements State{
    private final FileHelper fileHelper;
    @Autowired
    public CleaningState(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }

    @Override
    public State process(Resource resource) {
        File file = resource.getFile();
        try {
            fileHelper.deleteFile(file.toPath());
            return null;
        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    @Override
    public AuditState nextState() {
        return CLEANING;
    }
}