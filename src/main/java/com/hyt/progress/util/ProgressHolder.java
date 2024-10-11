package com.hyt.progress.util;

import com.alibaba.ttl.TransmittableThreadLocal;

public class ProgressHolder {
    private static final TransmittableThreadLocal<String> PROGRESS_KEYS = new TransmittableThreadLocal<>();

    /**
     * 开始设置进度key
     *
     * @param key
     */
    public static void set(String key) {
        PROGRESS_KEYS.set(key);
    }

    public static String get() {
        return PROGRESS_KEYS.get();
    }

    public static void remove() {
        PROGRESS_KEYS.remove();
    }

    public static boolean exist() {
        return null == PROGRESS_KEYS.get() ? false : true;
    }
}
