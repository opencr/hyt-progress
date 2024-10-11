package com.hyt.progress.util;

import com.hyt.progress.entity.Progress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class ProgressRedis {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 进度条加锁，如果存在锁，则直接返回（业务中更新进度不需要使用该方法）
     *
     * @param key
     * @param timeout
     */
    public boolean lock(String key, long timeout) {
        boolean flag = redisTemplate.opsForValue().setIfAbsent(ProgressConstant.PROGRESS_LOCK_KEY + key, true, timeout, TimeUnit.SECONDS);
        return flag;
    }

    /**
     * 设置缓存过期时间（业务中不需要使用该方法）
     *
     * @param key
     * @param timeout
     */
    public void expire(String key, long timeout) {
        redisTemplate.expire(ProgressConstant.PROGRESS_KEYS + key, timeout, TimeUnit.SECONDS);
    }


    /**
     * 主动抛异常，中断进度，把异常信息抛给前端（业务中不需要使用该方法）
     *
     * @param errMsg
     */
    public void errProgress(String errMsg) {
        if (ProgressHolder.exist()) {
            Progress progress = new Progress();
            progress.setCurrent(100);
            progress.setSuccess(false);
            progress.setMsg(errMsg);
            updateCache(progress);
        }
    }

    /**
     * 更新进度
     * @param current
     * @param msg
     * @param data
     */
    public void updateProgressCurrent(int current, String msg, Object data) {
        if (ProgressHolder.exist() && current <= 100) {
            Object o = redisTemplate.opsForValue().get(ProgressConstant.PROGRESS_KEYS + ProgressHolder.get());
            Progress progress = new Progress();
            if(o != null) {
                progress = (Progress) o;
            }
            if(progress.getCurrent() >= current) {
                return;
            }
            progress.setCurrent(current);
            if (msg != null) {
                progress.setMsg(msg);
            }
            if(data != null) {
                progress.setData(data);
            }
            this.updateCache(progress);
        }
    }

    /**
     * 释放锁（业务中不需要使用该方法）
     *
     * @param key
     */
    public void releaseLock(String key) {
        redisTemplate.delete(ProgressConstant.PROGRESS_LOCK_KEY + key);
    }

    /**
     * 释放锁（业务中不需要使用该方法）
     *
     * @param key
     */
    public void deleteProgress(String key) {
        redisTemplate.delete(ProgressConstant.PROGRESS_KEYS + key);
    }

    public void updateCache(Progress progress) {
//        log.info("#当前进度条：{}==={}", ProgressHolder.get(), JSON.toJSON(progress));
        redisTemplate.opsForValue().set(ProgressConstant.PROGRESS_KEYS + ProgressHolder.get(), progress);
    }

}
