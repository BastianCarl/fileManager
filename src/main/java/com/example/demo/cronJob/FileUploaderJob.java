package com.example.demo.cronJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
@Component
public class FileUploaderJob implements Job {
    private final FileUploadService fileUploadService;

    public FileUploaderJob(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }
    @Override
    public void execute(JobExecutionContext arg0) {
        fileUploadService.manageFilesProcessing();
    }
}