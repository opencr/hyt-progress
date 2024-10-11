package com.hyt.progress.keyCreator;


import com.hyt.progress.annotation.HytProgress;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.multipart.MultipartFile;

public interface ProgressLockKeyCreator {
    String createLockKey(ProceedingJoinPoint pjp, HytProgress progress);

    /**
     * 根据参数名获取参数值
     *
     * @param pjp
     * @param paramName 参数名
     * @return
     */
    default Object getQueryParamValue(ProceedingJoinPoint pjp, String paramName) {
        Object[] values = pjp.getArgs();
        String[] names = ((CodeSignature) pjp.getSignature()).getParameterNames();
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(paramName)) {
                return values[i];
            }
        }
        return null;
    }

    /**
     * 根据参数名获取参数值
     *
     * @param pjp
     * @param paramName 参数名
     * @return
     */
    default Object getBodyParamValue(ProceedingJoinPoint pjp, String paramName) {
        Object[] values = pjp.getArgs();
        String[] names = ((CodeSignature) pjp.getSignature()).getParameterNames();
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(paramName)) {
                if(values[i] instanceof MultipartFile) {
                    return ((MultipartFile) values[i]).getOriginalFilename();
                } else {
                    return values[i];
                }
            }
        }
        return null;
        /*Object body = values[0];
        try {
            return ReflectUtil.getFieldValue(body, paramName);
        } catch (Exception e) {
            return null;
        }*/
    }

    /**
     * 获取controller类名
     *
     * @param pjp
     * @return
     */
    default String getClassName(ProceedingJoinPoint pjp) {
        return pjp.getTarget().getClass().getName();
    }

    /**
     * 获取接口方法名
     *
     * @param pjp
     * @return
     */
    default String getMethodName(ProceedingJoinPoint pjp) {
        return ((MethodSignature) pjp.getSignature()).getMethod().getName();
    }
}
