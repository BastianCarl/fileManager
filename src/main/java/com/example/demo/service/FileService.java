package com.example.demo.service;

import com.example.demo.aws.AwsClient;
import com.example.demo.model.FileMetadata;
import com.example.demo.repository.FileMetadataRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
}
