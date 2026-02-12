package com.example.demo.service;
import com.example.demo.exception.DatabaseFailure;
import com.example.demo.files.AwsImplementationFileService;
import com.example.demo.model.FileMetadata;
import com.example.demo.repository.FileMetadataRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Service
@AllArgsConstructor
public class FileMetaDataService {

    private final FileMetadataRepository repository;
    private final AwsImplementationFileService awsImplementationFileService;
    private static Logger LOGGER = LoggerFactory.getLogger(FileMetaDataService.class);
    public FileMetadata uploadFileMetaData(FileMetadata metadata){
        try {
            return repository.save(metadata);
        }catch (Exception e) {
            throw new DatabaseFailure();
        }
    }

    public boolean checkFileExists(FileMetadata file){
        try {
            return repository.findByHashValue(file.getHashValue()) != null;
        } catch (DataAccessException e) {
            throw new DatabaseFailure();
        }
    }

    public void deleteFileMetaData(FileMetadata file) {
        repository.deleteById(file.getId());
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