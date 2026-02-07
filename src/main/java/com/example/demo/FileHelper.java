package com.example.demo;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;


@Component

public class FileHelper {

    public void copyFile(File source, Path target) throws IOException {
        Path sourcePath = source.toPath();
        Path targetPath = Path.of(target.toString(), source.getName());
        Files.copy(
                sourcePath,
                targetPath,
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    public void move(File file, Path destination) {
        try {
            createDirectory(destination);
            copyFile(file, destination);
            deleteFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createDirectory(Path destination) throws IOException {
        Files.createDirectories(destination);
    }

    public void deleteFile(File file) throws IOException {
        Files.delete(file.toPath());
    }

    public void deleteFilesOnly(Path directoryPath) {
        File dir = new File(String.valueOf(directoryPath));
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                f.delete();
            }
        }
    }

    public File[] listFiles(File directory) {
        return directory.listFiles() != null ? directory.listFiles() : new File[0];
    }

    public void createFiles(List<MultipartFile> files, Path backupDirectoryWithDate) {
        try {
            for (MultipartFile file : files) {
                Path destination = Path.of(backupDirectoryWithDate.toString(), file.getOriginalFilename());
                file.transferTo(destination);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}