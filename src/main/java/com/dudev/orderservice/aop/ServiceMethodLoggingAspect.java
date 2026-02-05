package com.dudev.orderservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ServiceMethodLoggingAspect {

    @Around("execution(* com.dudev.orderservice.service.impl.*.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        log.info("START invocation method={}", methodName);
        Object result;
        try {
            result = joinPoint.proceed();
            return result;
        } finally {
            log.info("END invocation method={}", methodName);
        }
    }
}