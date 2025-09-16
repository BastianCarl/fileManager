package com.example.demo.repository;

import com.example.demo.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findByOwnerId(Long ownerId);
}
