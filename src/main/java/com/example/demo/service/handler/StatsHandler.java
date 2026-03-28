package com.example.demo.service.handler;

import com.example.demo.repository.model.StatsContext;
import com.example.demo.repository.model.StatsType;

public interface StatsHandler {

    StatsType type();

    Object execute(StatsContext context);
}