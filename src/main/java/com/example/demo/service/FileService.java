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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@AllArgsConstructor
public class FileService {

    private final FileMetadataRepository repository;
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

    public byte[] downloadAllFiles(Long ownerId) throws IOException {
        List<FileMetadata> files =  repository.findByOwnerId(ownerId);
        Map<String, byte[]> filesMap = new HashMap<>();
        for (FileMetadata fileMetadata : files) {
            String awsUrl = AwsClient.getFile(AwsClient.generateAwsKey(fileMetadata));
            filesMap.put(fileMetadata.getName(), downloadFile(awsUrl));
        }
        return createZip(filesMap);
    }

    public byte[] downloadFile(String awsUrl) throws IOException {
        URL url = new URL(awsUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        try (InputStream in = con.getInputStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            byte[] data = new byte[8192]; // buffer de 8KB
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
        for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                ZipEntry element = new ZipEntry(entry.getKey());
                zos.putNextEntry(element);
                zos.write(entry.getValue());
                zos.closeEntry();
        }
        zos.close();
        return baos.toByteArray();
    }
}
