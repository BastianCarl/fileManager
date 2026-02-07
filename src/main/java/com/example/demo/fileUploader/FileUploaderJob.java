package com.example.demo.fileUploader;
import com.example.demo.FileHelper;
import jakarta.annotation.PostConstruct;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.File;
@Component
public class FileUploaderJob implements Job {

    private final FileUploaderService fileUploaderService;
    private final FileHelper fileHelper;
    private File directory;
    @Value("${file.uploader.job.pending.path}")
    private String PENDING_PATH;
    @Autowired
    public FileUploaderJob(
            FileUploaderService fileUploaderService
    ) {
       this.fileUploaderService = fileUploaderService;
       this.fileHelper = new FileHelper();
    }

    @PostConstruct
    public void init() {
        directory = new File(PENDING_PATH);
    }

    @Override
    public void execute(JobExecutionContext arg0) {
        for (File file : fileHelper.listFiles(directory)) {
            fileUploaderService.process(file);
        }
    }
}