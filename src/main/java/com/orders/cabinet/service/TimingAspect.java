package com.orders.cabinet.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimingAspect {

    @Around("@annotation(com.orders.cabinet.event.Timed)")
    public Object timeMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();

        long time = System.currentTimeMillis() - start;
        log.info("TIMED: Method {} finished in {}ms", joinPoint.getSignature().toShortString(), time);

        return proceed;
    }
}

