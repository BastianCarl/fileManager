package com.example.demo.fileState;

import com.example.demo.FileHelper;
import com.example.demo.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CleaningState implements State{
    private final FileHelper fileHelper;
    @Autowired
    public CleaningState(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }

    @Override
    public AuditStateState process(Resource resource) {
        File file = (File) resource.getSource();
        try {
            fileHelper.deleteFile(file.toPath());
            return null;
        }catch (Exception e){
            throw new RuntimeException();
        }
    }
}