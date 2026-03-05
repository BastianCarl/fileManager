package com.example.demo.fileUploader;
import com.example.demo.model.Animal;
import com.example.demo.model.Car;
import com.example.demo.repository.AnimalRepository;
import com.example.demo.repository.CarRepository;
import com.example.demo.utility.FileHelper;
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
    private final CarRepository carRepository;
    private final FileHelper fileHelper;
    private final AnimalRepository animalRepository;
    @Value("#{T(java.nio.file.Paths).get('${file.uploader.job.pending.path}')}")
    private Path pendingPath;
    @Value("#{T(java.nio.file.Paths).get('${file.uploader.job.working.path}')}")
    private Path workingPath;
    @Autowired
    public FileUploaderJob(
            FileUploaderService fileUploaderService, CarRepository carRepository, AnimalRepository animalRepository) {
       this.fileUploaderService = fileUploaderService;
       this.carRepository = carRepository;
       this.fileHelper = new FileHelper();
        this.animalRepository = animalRepository;
    }

    @PostConstruct
    public void init() {
        fileHelper.checkDirectory(pendingPath);
        fileHelper.checkDirectory(workingPath);
    }

    @Override
    public void execute(JobExecutionContext arg0) {
        for (int i=1; i<=10; i++) {
            carRepository.save(new Car(String.valueOf(i)));
        }

        for (int i=1; i<=10; i++) {
            animalRepository.save(new Animal(String.valueOf(i)));
        }

        fileHelper.copyFolder(pendingPath, workingPath);
        for (File file : fileHelper.listFiles(workingPath)) {
            fileUploaderService.process(file);
        }
        fileHelper.deleteAllFiles(pendingPath);
    }
}