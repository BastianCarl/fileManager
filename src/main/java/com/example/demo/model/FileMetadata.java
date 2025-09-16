package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "file_metadata")
@Data
@NoArgsConstructor
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String mimeType;
    private Long ownerId;
    private Long size;
    private String awsKey;

    public FileMetadata(String name, String mimeType, Long ownerId, Long size, String awsKey) {
        this.name = name;
        this.mimeType = mimeType;
        this.ownerId = ownerId;
        this.size = size;
        this.awsKey = awsKey;
    }
}
