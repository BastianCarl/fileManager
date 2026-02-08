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

    public void copyFile(Path source, Path targetDirectory) throws IOException {
        Path targetPath = targetDirectory.resolve(source.getFileName());
        Files.copy(
                source,
                targetPath,
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    public void move(Path source, Path destination) {
        try {
            createDirectory(destination);
            copyFile(source, destination);
            deleteFile(source);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createDirectory(Path destination) throws IOException {
        Files.createDirectories(destination);
    }

    public void deleteFile(Path file) throws IOException {
        Files.delete(file);
    }

    public void deleteFilesOnly(Path directoryPath) {
        try (DirectoryStream<Path> files = Files.newDirectoryStream(directoryPath)) {
            for (Path f : files) {
                if (Files.isRegularFile(f)) {
                    Files.delete(f);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File[] listFiles(Path path) {
        File directory = path.toFile();
        return directory.listFiles() != null ? directory.listFiles() : new File[0];
    }

    public void createFiles(List<MultipartFile> files, Path backupDirectoryWithDate) {
        try {
            for (MultipartFile file : files) {
                Path destination = backupDirectoryWithDate.resolve(file.getOriginalFilename());
                file.transferTo(destination);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkDirectory(Path path) {
        if (!Files.exists(path) || !Files.isDirectory(path) || !Files.isReadable(path) || !Files.isWritable(path)) {
            throw new RuntimeException();
        }
    }

    public void copyFolder(Path sourceFolder, Path targetFolder) {
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