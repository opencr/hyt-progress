package com.hyt.progress.keyCreator;

import com.hyt.progress.annotation.HytProgress;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * 自定义锁的key
 **/
@ConditionalOnMissingBean(ProgressLockKeyCreator.class)
@Component
@Slf4j
public class ComQueryProgressLockKeyCreator implements ProgressLockKeyCreator {
    @Override
    public String createLockKey(ProceedingJoinPoint pjp, HytProgress progress) {
        String className = pjp.getTarget().getClass().getName();
        String methodName = ((MethodSignature) pjp.getSignature()).getMethod().getName();
        String key = className + methodName + getQueryParamValue(pjp, "projectId");
        log.info("# 通用query请求key:{}", key);
        return key;
    }

}
