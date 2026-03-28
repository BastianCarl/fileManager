package com.example.demo.repository.model.dto;

public record DailyUploadDTO(
        java.time.LocalDate day,
        Long uploadCount,
        Long totalSize
) {}