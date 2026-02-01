package com.example.demo.aspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.File;
@Component
@Aspect
public class LoggingAspect {
    private static Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);
//    @Before(
//            "execution(* com.example.demo.controller.FileController.downloadFile(..)) || " +
//            "execution(* com.example.demo.controller.FileController.uploadFile(..)) || " +
//            "execution(* com.example.demo.controller.FileController.search(..)) || " +
//            "execution(* com.example.demo.controller.FileController.getAllFiles(..))"
//    )
//    public void logMethodCall(JoinPoint joinPoint) {
//        LOGGER.info("Method call " + joinPoint.getSignature().getName());
//    }

    @AfterReturning("execution(* com.example.demo.cronJob.FileUploadService.manageFileProcessing(..))")
    public void logFileUploadIfSuccessful(JoinPoint joinPoint) {
        File file = (File) joinPoint.getArgs()[0];
        LOGGER.info("File {} successfully uploaded", file.getName());
    }

    @Before("execution(* com.example.demo.files.FileServiceOrchestrator.restoreFolder(..))")
    public void logRestoreProcedureCalled(JoinPoint joinPoint) {
        String date = (String) joinPoint.getArgs()[1];
        LOGGER.info("Restore Procedure was called for {}", date);
    }

    @AfterReturning("execution(* com.example.demo.files.FileServiceOrchestrator.restoreFolder(..))")
    public void logIfRestoreProcedureSuccessful(JoinPoint joinPoint) {
        String date = (String) joinPoint.getArgs()[1];
        LOGGER.info("Restore Procedure was successful for {}", date);
    }
}