package com.hyt.progress.annotation;

import com.hyt.progress.keyCreator.DefaultProgressLockKeyCreator;
import com.hyt.progress.keyCreator.ProgressLockKeyCreator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 进度条配置注解，放在接口方法上，注意约定进度要100
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HytProgress {

    /**
     * 进度条接口是否允许并发调用，默认允许
     */
    boolean canConcurrent() default true;

    String errMsg() default "操作失败";

    /**
     * 进度条超时时间，单位秒
     */
    long timeoutSeconds() default 60;

    /**
     * 进度条不允许并发时锁定的key，支持自定义，默认为方法级锁
     *
     * @return
     */
    Class<? extends ProgressLockKeyCreator> lockKeyCreator() default DefaultProgressLockKeyCreator.class;
}

