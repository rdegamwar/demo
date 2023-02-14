package com.example.trade.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Aspect
@Component
@Slf4j
@ConditionalOnExpression("${aspect.enabled:true}")
public class ExecutionTimeAdvice {

    @Around("@annotation(com.example.trade.aspect.TrackExecutionTime)")
    public Object executionTime(ProceedingJoinPoint point) throws Throwable {
        Instant before = Instant.now();
        Object object = point.proceed();
        Instant after = Instant.now();
        long delta = Duration.between(before, after).toNanos();
        log.info("Class Name: "+ point.getSignature().getDeclaringTypeName() +". Method Name: "+ point.getSignature().getName() + ". Time taken for Execution is : " + (delta) +"nanos");
        return object;
    }
}
