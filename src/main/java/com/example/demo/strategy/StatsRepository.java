package com.example.demo.strategy;


import java.util.List;

public interface StatsRepository {

    List<TopUploaderDTO> topUploaders(Long from, int limit);

    List<DailyUploadDTO> uploadsPerDay(Long from, Long to);

    List<ExtensionStatsDTO> groupByExtension(Long from, Long to);

    List<FailedUploadDTO> failedUploads(Long from);
}