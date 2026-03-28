package com.example.demo.service.handler;

import com.example.demo.repository.StatsRepository;
import com.example.demo.model.StatsContext;
import com.example.demo.model.StatsType;
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

        return repository.failedUploads(from, ctx.fileUploaderClient());
    }
}