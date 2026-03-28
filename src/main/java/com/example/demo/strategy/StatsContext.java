package com.example.demo.strategy;

public record StatsContext(
        int days,
        int top,
        boolean groupByExtension
) {}