package com.example.demo.service;
import com.example.demo.exception.DuplicatedFile;
import com.example.demo.files.AwsImplementationFileService;
import com.example.demo.model.FileMetadata;
import com.example.demo.repository.FileMetadataRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Service
@AllArgsConstructor
public class FileMetaDataService {

    private final FileMetadataRepository repository;
    private final AwsImplementationFileService awsImplementationFileService;

    private static Logger LOGGER = LoggerFactory.getLogger(FileMetaDataService.class);
    public FileMetadata uploadFileMetaData(MultipartFile file, Long ownerId) throws IOException {
//        validateFile(file);
        FileMetadata metadata = new FileMetadata(file.getOriginalFilename(), file.getContentType(), ownerId, file.getSize(), awsImplementationFileService.generateKey(file));
        metadata.setHashValue(FileMetadata.generateHashValue(file.getBytes()));
        return repository.save(metadata);
    }

    public FileMetadata uploadFileMetaData(File file, Long ownerId) throws IOException, DuplicatedFile {
//        validateFile(file);
        FileMetadata metadata = new FileMetadata(file.getName(), Files.probeContentType(file.toPath()), ownerId, file.length(), awsImplementationFileService.generateKey(file));
        metadata.setHashValue(FileMetadata.generateHashValue(Files.readAllBytes(file.toPath())));
        if (repository.findByHashValue(metadata.getHashValue()) == null) {
            return repository.save(metadata);
        } else {
            throw new DuplicatedFile("File " + metadata.getName() + " already exists");
        }
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
}