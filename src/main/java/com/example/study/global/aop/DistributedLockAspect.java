package com.example.study.global.aop;

import com.example.study.global.annotation.DistributedLock;
import com.example.study.global.exception.BusinessException;
import com.example.study.global.exception.CommonErrorCode;
import com.example.study.global.util.CustomSpringELParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(com.example.study.global.annotation.DistributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        // SpEL 파싱을 통해 동적으로 락 키를 생성
        String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(
                signature.getParameterNames(),
                joinPoint.getArgs(),
                distributedLock.key()
        );

        RLock rLock = redissonClient.getLock(key);

        try {
            // 락 획득 시도
            boolean available = rLock.tryLock(
                    distributedLock.waitTime(),
                    distributedLock.leaseTime(),
                    distributedLock.timeUnit()
            );

            if (!available) {
                log.warn("Redisson Lock Not Available. key: {}", key);
                throw new BusinessException(CommonErrorCode.LOCK_ACQUISITION_FAILED);
            }

            // 트랜잭션이 보장된 상태에서 타겟 메서드 실행
            return aopForTransaction.proceed(joinPoint);

        } catch (InterruptedException e) {
            log.error("Redisson Lock Interrupted. key: {}", key);
            Thread.currentThread().interrupt();
            throw new BusinessException(CommonErrorCode.LOCK_INTERRUPTED);
        } finally {
            try {
                // leaseTime이 만료되어 이미 락이 해제되었을 경우의 예외 방지
                rLock.unlock();
                log.info("Lock 해제 성공 - Key: {}", key);
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already Unlocked - Method: {}, Key: {}", method.getName(), key);
            }
        }
    }
}
