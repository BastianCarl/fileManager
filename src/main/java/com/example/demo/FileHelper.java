package com.example.demo;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
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

    public void checkDirectory(Path path) {
        File file = new File(String.valueOf(path));
        if (!file.exists() || !file.isDirectory() || !file.canRead() || !file.canWrite()) {
            throw new RuntimeException();
        }
    }

    public void copyFolder(Path sourceFolder, Path targetFolder){
        try {
            createDirectory(targetFolder);
            try (DirectoryStream<Path> files = Files.newDirectoryStream(sourceFolder)) {
                for (Path file : files) {
                    if (Files.isRegularFile(file)) {
                        Path targetFile = targetFolder.resolve(file.getFileName());
                        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAllFiles(Path folder) {
        try (DirectoryStream<Path> files = Files.newDirectoryStream(folder)) {
            for (Path file : files) {
                if (Files.isRegularFile(file)) {
                    Files.delete(file);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}