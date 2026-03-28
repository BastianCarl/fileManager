package com.example.demo.repository.model.dto;

public record FailedUploadDTO(
        String code,
        Long ownerId,
        Long uploadTime,
        String step
) {}