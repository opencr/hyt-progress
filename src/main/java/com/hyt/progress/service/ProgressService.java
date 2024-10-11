package com.hyt.progress.service;


import com.hyt.progress.entity.Progress;
import com.hyt.progress.util.ProgressConstant;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ProgressService {
    @Resource
    private RedisTemplate redisTemplate;

    public Progress get(String key) {
        Object o = redisTemplate.opsForValue().get(ProgressConstant.PROGRESS_KEYS + key);
        if(o != null && o instanceof Progress) {
            return (Progress) o;
        } else {
            return Progress.builder().current(0).success(true).build();
        }
    }
}
