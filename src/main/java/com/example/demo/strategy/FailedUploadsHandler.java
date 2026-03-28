package com.example.demo.strategy;

import org.springframework.stereotype.Component;

@Component
public class FailedUploadsHandler implements StatsHandler {

    private final StatsRepository repository;

    public FailedUploadsHandler(StatsRepository repository) {
        this.repository = repository;
    }

    @Override
    public StatsType type() {
        return StatsType.FAILED_UPLOADS;
    }

    @Override
    public Object execute(StatsContext ctx) {

        long now = System.currentTimeMillis();
        long from = now - ctx.days() * 86400000L;

        return repository.failedUploads(from);
    }
}