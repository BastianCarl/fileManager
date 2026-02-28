package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Table(uniqueConstraints = {@UniqueConstraint(name = "version_name", columnNames = { "version", "name" })})
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
    private String key;
    private String hashValue;
    private String code;
    private Long version;
    public FileMetadata(String name, String mimeType, Long ownerId, Long size, String key, String hashValue) {
        this.name = name;
        this.mimeType = mimeType;
        this.ownerId = ownerId;
        this.size = size;
        this.key = key;
        this.hashValue = hashValue;
        this.code = generateCode(ownerId, size, key, hashValue);
    }


    public String generateCode(Long ownerId, Long size, String key, String hashValue) {
        try {
            String combined = ownerId + "|" + size + "|" + key + "|" + hashValue;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not found", e);
        }
    }
}
