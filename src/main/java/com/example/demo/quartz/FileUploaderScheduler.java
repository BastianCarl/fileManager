//package com.example.demo.quartz;
//
//import org.quartz.CronTrigger;
//import org.quartz.JobDetail;
//import org.quartz.Scheduler;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class FileUploaderScheduler {
//    private final JobDetail jobDetail;
//    private final CronTrigger cronTrigger;
//    private final Scheduler scheduler;
//
//    @Autowired
//    public FileUploaderScheduler(JobDetail jobDetail, CronTrigger cronTrigger, Scheduler scheduler){
//        this.jobDetail = jobDetail;
//        this.cronTrigger = cronTrigger;
//        this.scheduler = scheduler;
//    }
//}