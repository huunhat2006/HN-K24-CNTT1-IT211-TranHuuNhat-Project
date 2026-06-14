package com.restaurant.hnks24cntt1it211tranhuunhatproject.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PerformanceLoggingAspect {

    // Khai báo Pointcut quét qua toàn bộ phương thức trong gói controller và service
    @Around("execution(* com.restaurant.hnks24cntt1it211tranhuunhatproject.service..*.*(..)) || " +
            "execution(* com.restaurant.hnks24cntt1it211tranhuunhatproject.controller..*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        // Cho phép phương thức gốc chạy tiếp tục
        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        // In log ra màn hình Console theo thời gian thực
        log.info("[PERFORMANCE LOG] Phương thức [{}] chạy hết: {} ms", 
                joinPoint.getSignature().toShortString(), executionTime);
        
        return proceed;
    }
}