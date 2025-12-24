package com.marcosespeche.spring_batch_poc.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.marcosespeche.spring_batch_poc.domain..*Service.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();

        log.info("→ {}.{}() called with args: {}", className, methodName, Arrays.toString(args));

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();

            log.info("← {}.{}() returned: {} (took {}ms)",
                    className, methodName, result.toString(), stopWatch.getTotalTimeMillis());

            return result;
        } catch (Exception e) {
            stopWatch.stop();
            log.error("✗ {}.{}() threw exception after {}ms: {}",
                    className, methodName, stopWatch.getTotalTimeMillis(), e.getMessage(), e);
            throw e;
        }
    }

    @Around("execution(* com.marcosespeche.spring_batch_poc..controller..*.*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        log.info("HTTP → {}.{}()", className, methodName);

        try {
            Object result = joinPoint.proceed();
            log.info("HTTP ← {}.{}() completed successfully", className, methodName);
            return result;
        } catch (Exception e) {
            log.error("HTTP ✗ {}.{}() failed: {}", className, methodName, e.getMessage());
            throw e;
        }
    }
}
