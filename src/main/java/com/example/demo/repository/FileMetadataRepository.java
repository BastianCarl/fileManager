package com.example.demo.repository;

import com.example.demo.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findByOwnerId(Long ownerId);
    FileMetadata findByName(String name);
    void deleteById(Long id);
    boolean existsByHashValue(String hashValue);
    @Query("""
       SELECT COALESCE(MAX(f.version), 0)
       FROM file_metadata f
       WHERE f.name = :name
       """)
    Long findMaxVersionByName(@Param("name") String name);
    @Query("""
        SELECT f
        FROM file_metadata f
        WHERE f.version = (
            SELECT MAX(f2.version)
            FROM file_metadata f2
            WHERE f2.name = f.name
        )
    """)
    List<FileMetadata> findLatestVersion();
}