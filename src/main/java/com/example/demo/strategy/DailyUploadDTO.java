package com.example.demo.strategy;

public record DailyUploadDTO(
        java.time.LocalDate day,
        Long uploadCount,
        Long totalSize
) {}