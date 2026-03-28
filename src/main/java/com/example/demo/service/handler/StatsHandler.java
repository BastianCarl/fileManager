package com.example.demo.service.handler;

import com.example.demo.model.StatsContext;
import com.example.demo.model.StatsType;

public interface StatsHandler {

  StatsType type();

  Object execute(StatsContext context);
}
