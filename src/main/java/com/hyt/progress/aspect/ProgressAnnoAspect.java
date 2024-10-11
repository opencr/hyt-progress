package com.hyt.progress.aspect;

import com.hyt.progress.util.ProgressConstant;
import com.hyt.progress.keyCreator.ProgressCreator;
import com.hyt.progress.entity.Result;
import com.hyt.progress.annotation.HytProgress;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Component
@Order
public class ProgressAnnoAspect {

    @Resource
    private ProgressCreator progressCreator;

    @Around("@annotation(hytProgress)")
    public Object doAround(ProceedingJoinPoint pjp, HytProgress hytProgress) {
        String progressKey = progressCreator.createAndInvokeProgress(pjp, hytProgress);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 由于接口方法返回值统一void了 这里不能通过return返回值 只能通过请求头带出去
        request.setAttribute(ProgressConstant.PROGRESS_KEY, Result.success(progressKey));
        log.info("==ProgressAnnoAspect==【{}】", progressKey);
        return progressKey;
    }

}
