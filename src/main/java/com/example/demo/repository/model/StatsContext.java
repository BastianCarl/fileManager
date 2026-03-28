package com.example.demo.repository.model;

public record StatsContext(
        int days,
        int top,
        boolean groupByExtension
) {}