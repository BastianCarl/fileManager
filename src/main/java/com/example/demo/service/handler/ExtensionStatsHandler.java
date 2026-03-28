package com.example.demo.service.handler;

import com.example.demo.model.StatsContext;
import com.example.demo.model.StatsType;
import com.example.demo.repository.StatsRepository;
import org.springframework.stereotype.Component;

@Component
public class ExtensionStatsHandler implements StatsHandler {

  private final StatsRepository repository;

  public ExtensionStatsHandler(StatsRepository repository) {
    this.repository = repository;
  }

  @Override
  public StatsType type() {
    return StatsType.BY_EXTENSION;
  }

  @Override
  public Object execute(StatsContext ctx) {

    long now = System.currentTimeMillis();
    long from = now - ctx.days() * 86400000L;

    return repository.groupByExtension(from, now);
  }
}
