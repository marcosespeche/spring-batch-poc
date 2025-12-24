package com.marcosespeche.spring_batch_poc.aspects;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.marcosespeche.spring_batch_poc.domain..*Service.*(..))")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        long start = System.currentTimeMillis();

        if (log.isDebugEnabled()) {
            log.debug("{}.{}() started", className, methodName);
        }

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;

            if (log.isDebugEnabled()) {
                log.debug("{}.{}() finished in {}ms", className, methodName, duration);
            }

            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;

            if (isBusinessException(e)) {
                throw e;
            }

            log.error("{}.{}() failed after {}ms",
                    className, methodName, duration, e);

            throw e;
        }
    }

    private boolean isBusinessException(Exception e) {
        return
                e instanceof RuntimeException;
    }

}
