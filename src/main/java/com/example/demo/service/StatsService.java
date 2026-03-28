package com.example.demo.service;

import com.example.demo.model.StatsContext;
import com.example.demo.model.StatsType;
import com.example.demo.service.handler.StatsHandler;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StatsService {

  private final Map<StatsType, StatsHandler> handlers;

  public StatsService(List<StatsHandler> handlerList) {

    this.handlers = handlerList.stream().collect(Collectors.toMap(StatsHandler::type, h -> h));
  }

  public Object execute(StatsType type, StatsContext context) {

    StatsHandler handler = handlers.get(type);

    if (handler == null) {
      throw new IllegalArgumentException("No handler found for type: " + type);
    }

    return handler.execute(context);
  }
}
