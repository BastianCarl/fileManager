package com.example.demo.quartz;
import com.example.demo.files.FileServiceOrchestrator;
import com.example.demo.service.UserService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static com.example.demo.service.UserService.userDTO;

@Component
public class FileUploaderJob implements Job {
    private final UserService userService;
    private final FileServiceOrchestrator fileServiceOrchestrator;
    private static final String RESOURCE_PATH = "E:\\db\\demo\\src\\main\\java\\com\\example\\demo\\requests\\quartz";

    @Autowired
    public FileUploaderJob(UserService userService, FileServiceOrchestrator fileServiceOrchestrator) {
        this.userService = userService;
        this.fileServiceOrchestrator = fileServiceOrchestrator;
    }

    public void execute(JobExecutionContext arg0) {
        File directory = new File(RESOURCE_PATH);
        File[] files = directory.listFiles(File::isFile);
        assert files != null;
        for (File file : files) {
            try {
                fileServiceOrchestrator.uploadFile(file, userService.getOwnerId(userDTO));
                Files.delete(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException("Upload / delete failed: " + file.getName(),e);
            }
        }
    }
}