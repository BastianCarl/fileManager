package com.example.demo;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class FileUtils {
    public static void copyFile(File source, Path target) throws IOException {
        Path sourcePath = source.toPath();
        Path targetPath = Path.of(target.toString(), source.getName());
        Files.copy(
                sourcePath,
                targetPath,
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    public static void move(File file, Path destination) {
        try {
            createDirectory(destination);
            FileUtils.copyFile(file, destination);
            deleteFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createDirectory(Path destination) throws IOException {
        Files.createDirectories(destination);
    }

    public static void deleteFile(File file) throws IOException {
        Files.delete(file.toPath());
    }

    public static void deleteFilesOnly(Path directoryPath) {
        File dir = new File(String.valueOf(directoryPath));
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File f : files) {
            if (f.isFile()) {
                f.delete();
            }
        }
    }

    public static File[] listFiles(File directory) {
        return directory.listFiles() != null ? directory.listFiles() : new File[0];
    }

    public static void createFiles(List<MultipartFile> files, Path backupDirectoryWithDate) throws IOException {
        for (MultipartFile file : files) {
            Path destination = Path.of(backupDirectoryWithDate.toString(), file.getOriginalFilename());
            file.transferTo(destination);
        }
    }
}