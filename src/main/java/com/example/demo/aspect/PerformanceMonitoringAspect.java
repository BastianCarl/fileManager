//package com.example.demo.aspect;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//@Aspect
//@Component
//public class PerformanceMonitoringAspect {
//
//    private static Logger LOGGER = LoggerFactory.getLogger(PerformanceMonitoringAspect.class);
//
//    @Around("execution(* com.example.demo.aspect.Person.*(..))")
//    public Object monitorTime(ProceedingJoinPoint jp) throws Throwable {
//        long stat = System.currentTimeMillis();
//        Object object = jp.proceed();
//        long end = System.currentTimeMillis();
//        LOGGER.info("Time taken " + (end - stat));
//        return object;
//    }
//
//
//
//}
