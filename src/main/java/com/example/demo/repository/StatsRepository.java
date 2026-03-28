package com.example.demo.repository;


import com.example.demo.repository.model.dto.DailyUploadDTO;
import com.example.demo.repository.model.dto.ExtensionStatsDTO;
import com.example.demo.repository.model.dto.FailedUploadDTO;
import com.example.demo.repository.model.dto.TopUploaderDTO;

import java.util.List;

public interface StatsRepository {

    List<TopUploaderDTO> topUploaders(Long from, int limit);

    List<DailyUploadDTO> uploadsPerDay(Long from, Long to);

    List<ExtensionStatsDTO> groupByExtension(Long from, Long to);

    List<FailedUploadDTO> failedUploads(Long from);
}