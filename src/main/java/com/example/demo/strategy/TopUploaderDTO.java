package com.example.demo.strategy;

public record TopUploaderDTO(
        Long ownerId,
        String userName,
        Long uploadCount,
        Long totalSize
) {}