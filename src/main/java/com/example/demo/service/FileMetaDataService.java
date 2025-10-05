package com.example.demo.service;

import com.example.demo.files.AwsImplementationFileService;
import com.example.demo.model.FileMetadata;
import com.example.demo.repository.FileMetadataRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

@Service
@AllArgsConstructor
public class FileMetaDataService {

    private final FileMetadataRepository repository;
    private final ThreadManager threadManager;
    private final AwsImplementationFileService awsImplementationFileService;

    public FileMetadata uploadFileMetaData(MultipartFile file, Long ownerId) throws IOException {
        validateFile(file);
        FileMetadata metadata = new FileMetadata(file.getOriginalFilename(), file.getContentType(), ownerId, file.getSize(), awsImplementationFileService.generateKey(file));
        return repository.save(metadata);
    }

    public FileMetadata getFilesMetadata(Long fileId, Long ownerId) {
        List<FileMetadata> files =  repository.findByOwnerId(ownerId);
        return files.stream().filter(fileMetadata -> fileMetadata.getId().equals(fileId)).toList().getFirst();
    }

    public List<FileMetadata> getImageMetadataList(Long userId) {
        return repository.findByOwnerId(userId);
    }

    private void validateFile(MultipartFile file) {
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
//    public byte[] manageDownloadAllFilesAsArchive(Long ownerId) throws IOException {
//        List<FileMetadata> files = repository.findByOwnerId(ownerId);
//        return createZip(threadManager.downloadAllFiles(files));
//    }

//    public static byte[] downloadFile(String awsUrl) throws IOException {
//        URL url = new URL(awsUrl);
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        // move to constant
//        // use Spring to make GET
//        con.setRequestMethod("GET");
//        try (InputStream in = con.getInputStream();
//             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
//            // move to constant
//            byte[] data = new byte[8192];
//            int bytesRead;
//            while ((bytesRead = in.read(data)) != -1) {
//                buffer.write(data, 0, bytesRead);
//            }
//            return buffer.toByteArray();
//        } finally {
//            con.disconnect();
//        }
//    }

//    public static byte[] downloadFile(String awsUrl) {
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseExtractor<byte[]> responseExtractor = response -> {
//            try (InputStream in = response.getBody();
//                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//                byte[] buffer = new byte[8192];
//                int n;
//                while ((n = in.read(buffer)) != -1) {
//                    out.write(buffer, 0, n);
//                }
//                return out.toByteArray();
//            }
//        };
//
//        // Execută GET fără să folosească RequestCallback
//        return restTemplate.execute(awsUrl, HttpMethod.GET, null, responseExtractor);
//    }
}
