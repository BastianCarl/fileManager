package com.example.demo.quartz;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileUploaderConfig {
    private static final String JOB_NAME = "FileUploaderJob";
    private static final String TRIGGER_NAME = "FileUploaderTrigger";
    private static final String CRON_EXPRESSION = "* * * * * ?";

    @Bean
    public JobDetail getJob() {
        return JobBuilder.newJob(FileUploaderJob.class)
                .withIdentity(JOB_NAME)
                .storeDurably()
                .build();
    }

    @Bean
    public CronTrigger getTrigger() {
        return TriggerBuilder.newTrigger()
                .withIdentity(TRIGGER_NAME)
                .withSchedule(CronScheduleBuilder.cronSchedule(CRON_EXPRESSION))
                .forJob(JOB_NAME).build();
    }
}