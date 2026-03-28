package com.example.demo.model;

public record StatsContext(
        int days,
        int top,
        boolean groupByExtension,
        FileUploaderClient fileUploaderClient
) {}