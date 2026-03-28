package com.example.demo.model.dto;

public record FailedUploadDTO(String code, Long ownerId, Long uploadTime, String step) {}
