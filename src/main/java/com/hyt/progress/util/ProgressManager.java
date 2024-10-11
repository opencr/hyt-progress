package com.hyt.progress.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class ProgressManager {
    @Resource
    private ProgressRedis progressRedis;

    /**
     * 业务更新进度
     *
     * @param current 进度最高是100，前端判断100即是完成
     */
    public void updateProgress(int current) {
        progressRedis.updateProgressCurrent(current, null, null);
    }

    /**
     * 用于for循环进度更新,注意：step+from必须小于100
     * @param from  开始步长
     * @param to  总步长
     * @param size  for循环的size()
     * @param index for循环的i值，从0开始
     */
    public void stepProgress(int from, int to, int size, int index) {
        int current = from + (to  * (index + 1) / size);
//        log.info("进度条更新，当前进度：{}-{}-{}-{}-{}", from,  to, size, index, current);
        progressRedis.updateProgressCurrent(current, null, null);
    }

    /**
     * 进度完成后返回提示信息
     *
     * @param msg
     */
    public void finish(String msg) {
        progressRedis.updateProgressCurrent(100, msg, null);
    }

    /**
     * 进度条完成后返回数据
     * @param data
     */
    public void finish(Object data) {
        progressRedis.updateProgressCurrent(100, null, data);
    }
}
