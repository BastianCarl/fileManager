package com.example.demo.controller;

import com.example.demo.service.StatsService;
import com.example.demo.repository.model.StatsContext;
import com.example.demo.repository.model.StatsType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/uploads")
    public Object getUploadStats(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "10") int top,
            @RequestParam(defaultValue = "true") boolean groupByExtension,
            @RequestParam StatsType type   // IMPORTANT: alegi ce statistică vrei
    ) {

        StatsContext ctx = new StatsContext(days, top, groupByExtension);

        return statsService.execute(type, ctx);
    }
}