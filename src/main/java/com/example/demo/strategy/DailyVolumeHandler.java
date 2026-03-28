package com.example.demo.strategy;

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