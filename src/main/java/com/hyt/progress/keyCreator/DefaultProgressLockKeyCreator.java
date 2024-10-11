package com.hyt.progress.keyCreator;

import com.hyt.progress.annotation.HytProgress;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * 通过类名+方法名锁定
 **/
@ConditionalOnMissingBean(ProgressLockKeyCreator.class)
@Component
public class DefaultProgressLockKeyCreator implements ProgressLockKeyCreator {
    @Override
    public String createLockKey(ProceedingJoinPoint pjp, HytProgress progress) {
        String className = pjp.getTarget().getClass().getName();
        String methodName = ((MethodSignature) pjp.getSignature()).getMethod().getName();
        return className + methodName;
    }

}
