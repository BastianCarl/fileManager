package com.example.demo.service.handler;

import com.example.demo.repository.StatsRepository;
import com.example.demo.model.StatsContext;
import com.example.demo.model.StatsType;
import org.springframework.stereotype.Component;

@Component
public class DailyVolumeHandler implements StatsHandler {

    private final StatsRepository repository;

    public DailyVolumeHandler(StatsRepository repository) {
        this.repository = repository;
    }

    @Override
    public StatsType type() {
        return StatsType.DAILY_VOLUME;
    }

    @Override
    public Object execute(StatsContext ctx) {

        long now = System.currentTimeMillis();
        long from = now - ctx.days() * 86400000L;

        return repository.uploadsPerDay(from, now);
    }
}