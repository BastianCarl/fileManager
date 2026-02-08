package com.example.demo.fileUploader;
import com.example.demo.FileHelper;
import jakarta.annotation.PostConstruct;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.File;
import java.nio.file.Path;

@Component
public class FileUploaderJob implements Job {

    private final FileUploaderService fileUploaderService;
    private final FileHelper fileHelper;
    @Value("#{T(java.nio.file.Paths).get('${file.uploader.job.pending.path}')}")
    private Path pendingPath;
    @Value("#{T(java.nio.file.Paths).get('${file.uploader.job.working.path}')}")
    private Path workingPath;
    @Autowired
    public FileUploaderJob(
            FileUploaderService fileUploaderService
    ) {
       this.fileUploaderService = fileUploaderService;
       this.fileHelper = new FileHelper();
    }

    @PostConstruct
    public void init() {
        fileHelper.checkDirectory(pendingPath);
        fileHelper.checkDirectory(workingPath);
    }

    @Override
    public void execute(JobExecutionContext arg0) {
        fileHelper.copyFolder(pendingPath, workingPath);
        for (File file : fileHelper.listFiles(workingPath)) {
            fileUploaderService.process(file);
        }
        fileHelper.deleteAllFiles(pendingPath);
    }
}