package com.example.demo.fileUploadingSteps;

import com.example.demo.model.FileProcessingStep;
import com.example.demo.model.Resource;
import com.example.demo.utility.FileHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CleaningStep {
  private final FileHelper fileHelper;

  @Autowired
  public CleaningStep(FileHelper fileHelper) {
    this.fileHelper = fileHelper;
  }

  public FileProcessingStep process(Resource resource) {
    File file = resource.getFile();
    try {
      fileHelper.deleteFile(file.toPath());
    } catch (Exception e) {
      throw new RuntimeException();
    }
    return FileProcessingStep.DONE;
  }
}
