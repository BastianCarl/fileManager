package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorConfig {

    @Bean(name = "fileDownloadExecutor")
    public ExecutorService fileDownloadExecutor() {
        return new ThreadPoolExecutor(
                5,        // core threads
                5,                    // max threads (fix)
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(100), // coadă controlată
                new ThreadPoolExecutor.CallerRunsPolicy() // fallback
        );
    }
}