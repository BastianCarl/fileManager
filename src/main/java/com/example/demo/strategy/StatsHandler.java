package com.example.demo.strategy;

public interface StatsHandler {

    StatsType type();

    Object execute(StatsContext context);
}