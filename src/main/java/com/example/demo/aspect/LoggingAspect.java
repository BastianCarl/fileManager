package com.example.demo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
public class LoggingAspect {

    private static Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    /*
    return-type, class-name, method-name(args)
     */
    @Before("execution(* com.example.demo.aspect.Person.*(..))")
    public void logMethodCall(JoinPoint joinPoint) {
        LOGGER.info("Method call " + joinPoint.getSignature().getName());
    }

    @Before("execution(* com.example.demo.aspect.Person.*(..))")
    public void logMethodFinish(JoinPoint joinPoint) {
        LOGGER.info("Method finish " + Arrays.toString(joinPoint.getArgs()));
    }
}

