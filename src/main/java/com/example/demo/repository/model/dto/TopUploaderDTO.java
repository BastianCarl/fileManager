package com.example.demo.repository.model.dto;

public record TopUploaderDTO(
        Long ownerId,
        String userName,
        Long uploadCount,
        Long totalSize
) {}