package com.example.demo.fileUploader;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileUploaderConfig {
    private static final String JOB_NAME = "FileUploaderJob";
    private static final String TRIGGER_NAME = "FileUploaderTrigger";
    @Value("${file.uploader.job.expression}")
    private String CRON_EXPRESSION;
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