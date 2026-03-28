package com.example.demo.strategy;

public record FailedUploadDTO(
        String code,
        Long ownerId,
        Long uploadTime,
        String step
) {}