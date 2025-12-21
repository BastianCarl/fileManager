package com.example.demo.aspect;

import com.example.demo.model.UserDTO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ValidationAspect {

    private static Logger LOGGER = LoggerFactory.getLogger(ValidationAspect.class);

    @Around("execution(* com.example.demo.aspect.Person.*(..)) && args(number)")
    public Object validateAndUpdate(ProceedingJoinPoint jp, int number) throws Throwable {
        if (number < 0) {
            LOGGER.error("Invalid number");
            number= - number;
        }
        return jp.proceed(new Object[]{number});
    }
}
