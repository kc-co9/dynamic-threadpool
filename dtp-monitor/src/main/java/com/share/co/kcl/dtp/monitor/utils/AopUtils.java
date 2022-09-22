package com.share.co.kcl.dtp.monitor.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.reflect.Method;

public class AopUtils {

    private AopUtils() {
    }

    public static Class<?> getClass(JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass();
    }

    /**
     * 获取当前执行的方法
     */
    public static Method getMethod(JoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return joinPoint.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
    }

    /**
     * 获取有意义参数
     */
    public static Object[] getAvailableArgs(Object[] args) {
        int slow = 0;
        int fast = 0;
        for (; fast < args.length; ) {
            if (!checkObjectAvailable(args[fast])) {
                fast++;
                continue;
            }

            if (slow != fast) {
                args[slow] = args[fast];
            }
            slow++;
            fast++;
        }

        return ArrayUtils.subarray(args, 0, slow);
    }


    public static boolean checkObjectAvailable(Object object) {
        return null != object &&
                !(object instanceof MultipartFile ||
                        object instanceof ServletRequest ||
                        object instanceof ServletResponse);
    }
}
