package com.hyt.progress.keyCreator;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TtlRunnable;
import com.hyt.progress.annotation.HytProgress;
import com.hyt.progress.exception.ProgressException;
import com.hyt.progress.util.CopyMultipartFile;
import com.hyt.progress.util.ProgressHolder;
import com.hyt.progress.util.ProgressRedis;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.Future;


@Slf4j
@Component
public class ProgressCreator implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    @Resource
    private ProgressRedis progressRedis;
    @Resource
    private ThreadPoolTaskExecutor executor;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ProgressCreator.applicationContext = applicationContext;
    }

    /**
     * 获取或者创建进度条
     *
     * @param pjp      调用的接口
     * @param progress 接口上的进度条注解
     * @return 进度条
     */
    public String createAndInvokeProgress(ProceedingJoinPoint pjp, HytProgress progress) {
        String progressKey = null;
        if (progress.canConcurrent()) {
            // 允许重复执行
            progressKey = this.createKey(UUID.randomUUID().toString());
            this.invokeProgress(pjp, progress, progressKey);
        } else {
            // 不允许重复执行
            Class<? extends ProgressLockKeyCreator> lockKeyCreatorClass = progress.lockKeyCreator();
            if (!applicationContext.containsBean(lockKeyCreatorClass.getSimpleName())) {
                DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
                try {
                    defaultListableBeanFactory.registerSingleton(lockKeyCreatorClass.getSimpleName(), lockKeyCreatorClass.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("[进度条][创建进度条]进度条锁对象创建注册失败：", e);
                }
            }
            ProgressLockKeyCreator progressLockKeyCreator = applicationContext.getBean(lockKeyCreatorClass.getSimpleName(), ProgressLockKeyCreator.class);
            String lockKey = progressLockKeyCreator.createLockKey(pjp, progress);
            progressKey = this.createKey(lockKey);

            // 是否有正在执行的请求
            if (progressRedis.lock(progressKey, progress.timeoutSeconds())) {
                this.invokeProgress(pjp, progress, progressKey);
            } else {
                log.info("[进度条][创建进度条]不能并发的任务{} {}有人正在执行，直接返回进度条", lockKey, progressKey);
                return progressKey;
            }
        }
        return progressKey;
    }

    private void invokeProgress(ProceedingJoinPoint pjp, HytProgress hytProgress, String progressKey) {
        // 当前端传的对象是multiFile文件时这里需要拷贝一份传到业务方法 因为业务方法是另外的线程 不然就会报错
        Object[] values = copyMultiFile(pjp);

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        SecurityContext securityContext = SecurityContextHolder.getContext();

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;

        this.submit(() -> {
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servletRequestAttributes.getRequest()));
            // springSecurity上下文传递到业务线程
            SecurityContextHolder.setContext(securityContext);
            // 先释放更新进度，防止重复提交无法更新
            progressRedis.deleteProgress(progressKey);
            ProgressHolder.set(progressKey);
            try {
                pjp.proceed(values);
            } catch (Throwable e) {
                if (e instanceof ProgressException) {
                    progressRedis.errProgress(((ProgressException) e).getErrMsg());
                } else {
                    log.error("# 进度条任务往下执行失败：", e);
                    progressRedis.errProgress(hytProgress.errMsg());
                }
            } finally {
                // 执行完毕，释放锁，以便用户继续使用该功能，但是进度的缓存没有删除
                progressRedis.releaseLock(progressKey);
                // 设置缓存过期时间
                progressRedis.expire(progressKey, hytProgress.timeoutSeconds());
                ProgressHolder.remove();
                RequestContextHolder.resetRequestAttributes();
                SecurityContextHolder.clearContext();

            }
        });
    }

    public String createKey(String... args) {
        return Base64.encode(StrUtil.join(":", args), StandardCharsets.UTF_8);
    }

    public String[] parseKey(String key) {
        return Base64.decodeStr(key, StandardCharsets.UTF_8).split(":");
    }

    public Future<?> submit(Runnable task) {
        Runnable ttlRunnable = TtlRunnable.get(task);
        return executor.submit(ttlRunnable);
    }

    private Object[] copyMultiFile(ProceedingJoinPoint pjp) {
        Object[] values = pjp.getArgs();
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            if (value instanceof MultipartFile) {
                MultipartFile multipartFile = (MultipartFile) value;
                try {
                    values[i] = new CopyMultipartFile(multipartFile.getName(), multipartFile.getOriginalFilename(), multipartFile.getContentType(), multipartFile.getInputStream());
                } catch (Exception e) {

                }
            }
        }
        return values;
    }
}
