package com.example.demo.model.dto;

public record TopUploaderDTO(
        Long ownerId,
        String userName,
        Long uploadCount,
        Long totalSize
) {}