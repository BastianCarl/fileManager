package com.example.demo.aspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.File;
@Component
@Aspect
public class LoggingAspect {
    private static Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.example.demo.files.FileServiceOrchestrator.restoreBackup(..))")
    public void logRestoreBackupCall(JoinPoint joinPoint) {
        String date = (String) joinPoint.getArgs()[0];
        Long userId = (Long) joinPoint.getArgs()[1];
        LOGGER.info("FileController.restoreBackup called for {} by the userId = {}", date, userId);
    }

    @Before("execution(* com.example.demo.fileUploader.FileUploaderService.process(..))")
    public void logProcessCall(JoinPoint joinPoint) {
        File file = (File) joinPoint.getArgs()[0];
        LOGGER.info("FileUploaderService.process called for file {}", file.getAbsolutePath());
    }

    @AfterThrowing(pointcut = "execution(* com.example.demo.fileUploader.FileUploaderService.process(..))", throwing = "ex")
    public void logProcessFailure(JoinPoint joinPoint, Throwable ex) {
        File file = (File) joinPoint.getArgs()[0];
        LOGGER.warn("process failed for file {}", file.getAbsolutePath());
    }

    @AfterReturning(pointcut = "execution(* com.example.demo.fileUploader.FileUploaderService.process(..))")
    public void logProcessSuccessful(JoinPoint joinPoint) {
        File file = (File) joinPoint.getArgs()[0];
        LOGGER.info("FileUploaderService.process successful for file {}", file.getAbsolutePath());
    }

    @Before("execution(* com.example.demo.fileUploader.FileUploaderService.recover(..))")
    public void logRecoverCall(JoinPoint joinPoint) {
        File file = (File) joinPoint.getArgs()[1];
        Throwable ex = (Throwable) joinPoint.getArgs()[0];
        LOGGER.warn("Recover called for file {}", file.getAbsolutePath());
    }
}