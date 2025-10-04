package com.example.demo.service;

import com.example.demo.aws.AwsClient;
import com.example.demo.model.FileMetadata;
import com.example.demo.repository.FileMetadataRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@AllArgsConstructor
public class FileService {

    private final FileMetadataRepository repository;
    private static final Set<String> ACCEPTED_ARCHIVE_TYPES = new HashSet<>(List.of("zip"));
    private static final int LIMIT_FOR_DOWNLOADING = 1500;
    public FileMetadata uploadImage(MultipartFile file, Long ownerId) throws IOException {
        validateImage(file);
        FileMetadata metadata = new FileMetadata(file.getOriginalFilename(), file.getContentType(), ownerId, file.getSize(), AwsClient.generateAwsKey(file));
        return repository.save(metadata);
    }

    public FileMetadata getImageMetadata(Long fileId, Long ownerId) {
        List<FileMetadata> files =  repository.findByOwnerId(ownerId);
        return files.stream().filter(fileMetadata -> fileMetadata.getId().equals(fileId)).toList().getFirst();
    }

    public List<FileMetadata> getImageMetadataList(Long userId) {
        return repository.findByOwnerId(userId);
    }

    private void validateImage(MultipartFile file) {
        if(file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

//        String mimeType = file.getContentType();
//        if(mimeType == null || !ImagineStoreProperties.allowedMimeTypes.contains(mimeType)) {
//            throw new IllegalArgumentException("Invalid mime type.");
//        }
    }
    /*
     move Archive to other class
     File Service Interface(implement for Local and AWS)
     AwsClient.getFile(AwsClient.generateAwsKey(fileMetadata)) with multiple threads (5 fisiere in paralel) daca s-a terminat un fiser sa incepem altul fiser
     retry mechanism and logging for error
     */
    public byte[] downloadAllFilesAsArchive(Long ownerId) throws IOException {
        List<FileMetadata> files = repository.findByOwnerId(ownerId);
        int totalDownloaded = 0;
        byte[] file;
      var filesMap = new HashMap<String, byte[]>();
        for (FileMetadata fileMetadata : files) {
            String awsUrl = AwsClient.getFile(AwsClient.generateAwsKey(fileMetadata));
            file = downloadFile(awsUrl);
            // improve this computation
            // estimare dupa compresie
            totalDownloaded += file.length;
            filesMap.put(fileMetadata.getName(), file);
            if (totalDownloaded >= LIMIT_FOR_DOWNLOADING) {
                break;
            }
        }
        return createZip(filesMap);
    }

    public byte[] downloadFile(String awsUrl) throws IOException {
        URL url = new URL(awsUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        // move to constant
        // use Spring to make GET
        con.setRequestMethod("GET");
        try (InputStream in = con.getInputStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            // move to constant
            byte[] data = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(data)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            return buffer.toByteArray();
        } finally {
            con.disconnect();
        }
    }
    public static byte[] createZip( Map<String, byte[]> files) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        // use streams
        for (var entry : files.entrySet()) {
                ZipEntry element = new ZipEntry(entry.getKey());
                zos.putNextEntry(element);
                zos.write(entry.getValue());
                zos.closeEntry();
        }
        zos.close();
        return baos.toByteArray();
    }

    public boolean isArchiveTypeAccepted(String type) {
        return ACCEPTED_ARCHIVE_TYPES.contains(type);
    }
}
