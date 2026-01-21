package com.example.demo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Component
@Aspect
public class LoggingAspect {

    private static Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    /*
    return-type, class-name, method-name(args)
     */
    @Before(
            "execution(* com.example.demo.controller.FileController.downloadFile(..)) || " +
            "execution(* com.example.demo.controller.FileController.uploadFile(..)) || " +
            "execution(* com.example.demo.controller.FileController.search(..)) || " +
            "execution(* com.example.demo.controller.FileController.getAllFiles(..))"
    )
    public void logMethodCall(JoinPoint joinPoint) {
        LOGGER.info("Method call " + joinPoint.getSignature().getName());
    }
}