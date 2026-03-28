package com.example.demo.strategy;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final Map<StatsType, StatsHandler> handlers;

    public StatsService(List<StatsHandler> handlerList) {

        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(
                        StatsHandler::type,
                        h -> h
                ));
    }

    public Object execute(StatsType type, StatsContext context) {

        StatsHandler handler = handlers.get(type);

        if (handler == null) {
            throw new IllegalArgumentException("No handler found for type: " + type);
        }

        return handler.execute(context);
    }
}