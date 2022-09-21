package com.share.co.kcl.common.utils;

import com.share.co.kcl.common.generator.StringGenerator;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ReflectUtils.class);

    private ReflectUtils() {
    }

    public static String generateGetMethodName(String fieldName) {
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    public static String generateSetMethodName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    public static String generateBeanName(Class<?> clazz) {
        String className = clazz.getSimpleName();
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }

    public static Method generateGetMethod(Class<?> clazz, String fieldName) {
        Method getMethod;
        try {
            getMethod = clazz.getMethod(generateGetMethodName(fieldName));
        } catch (NoSuchMethodException e) {
            LOG.error("reflect invoke failure", e);
            throw new RuntimeException("reflect invoke failure");
        }
        return getMethod;
    }

    public static Method generateSetMethod(Class<?> clazz, String fieldName, Class<?> parameterTypes) throws NoSuchMethodException {
        Method setMethod;
        try {
            setMethod = clazz.getMethod(generateSetMethodName(fieldName), parameterTypes);
        } catch (NoSuchMethodException e) {
            LOG.error("reflect invoke failure", e);
            throw e;
        }
        return setMethod;
    }

    public static Object invokeGetMethod(Object o, String fieldName) {
        Method getMethod = generateGetMethod(o.getClass(), fieldName);
        try {
            getMethod.setAccessible(true);
            return getMethod.invoke(o);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error("reflect invoke failure", e);
            throw new RuntimeException("reflect invoke failure");
        }
    }

    public static void invokeSetMethod(Object o, String fieldName, Object args) {
        try {
            Method setMethod = generateSetMethod(o.getClass(), fieldName, args.getClass());
            setMethod.setAccessible(true);
            setMethod.invoke(o, args);
        } catch (Exception e) {
            LOG.error("reflect invoke failure", e);
            throw new RuntimeException("reflect invoke failure");
        }
    }

    public static void invokeSetMethod(Object o, String fieldName, Class<?> parameterTypes, Object args) {
        try {
            Method setMethod = generateSetMethod(o.getClass(), fieldName, parameterTypes);
            setMethod.invoke(o, args);
        } catch (Exception e) {
            LOG.error("reflect invoke failure", e);
            throw new RuntimeException("reflect invoke failure");
        }
    }

    public static <T> T replaceNewValue(Object oldObj, Object updateObj, Class<T> clazz) {
        Field[] fieldArray = clazz.getDeclaredFields();
        for (Field field : fieldArray) {
            Object value = ReflectUtils.invokeGetMethod(updateObj, field.getName());
            boolean isUpdate = Objects.nonNull(value);
            if (isUpdate) {
                ReflectUtils.invokeSetMethod(oldObj, field.getName(), value);
            }
        }

        @SuppressWarnings("unchecked")
        T t = (T) oldObj;
        return t;
    }

    public static boolean isNotPrimitive(Class<?> clazz) {
        return !isPrimitive(clazz);
    }

    public static boolean isPrimitive(Class<?> clazz) {
        return isBaseType(clazz) || isWrapBaseType(clazz);
    }

    /**
     * 判断是否是基础数据类型，即 int,double,long等类似格式
     */
    public static boolean isBaseType(Class clazz) {
        return clazz.isPrimitive();
    }

    /**
     * 判断是否是基础数据类型的包装类型
     */
    public static boolean isWrapBaseType(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    public static Object getPrivateValue(Object obj, String name) {
        try {
            // /通过类的字节码得到该类中声明的所有属性，无论私有或公有
            Field field = obj.getClass().getDeclaredField(name);
            // 设置访问权限
            field.setAccessible(true);

            // 得到私有的变量值
            return field.get(obj);
        } catch (Exception e) {
            LOG.error("reflect invoke failure", e);
            return null;
        }
    }

    public static Set<Class<?>> getInheritClass(Object obj) {
        if (Objects.isNull(obj)) {
            return Collections.emptySet();
        }

        Set<Class<?>> result = new HashSet<>();

        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            result.add(clazz);
            clazz = clazz.getSuperclass();
        }

        return result;
    }

    public static List<Field> getFieldFromClassSet(Set<Class<?>> classSet) {
        if (CollectionUtils.isEmpty(classSet)) {
            return Collections.emptyList();
        }
        return classSet.stream()
                .map(Class::getDeclaredFields)
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());
    }

    /**
     * 初始化对象
     */
    public static <T> void initializeBean(T t) {
        Class<?> clazz = t.getClass();
        Field[] fieldArr = clazz.getDeclaredFields();
        for (Field field : fieldArr) {
            if (field.getType() == Integer.class) {
                invokeSetMethod(t, field.getName(), 0);
            } else if (field.getType() == Long.class) {
                invokeSetMethod(t, field.getName(), 0L);
            } else if (field.getType() == Short.class) {
                invokeSetMethod(t, field.getName(), 0);
            } else if (field.getType() == Boolean.class) {
                invokeSetMethod(t, field.getName(), Boolean.FALSE);
            } else if (field.getType() == Float.class) {
                invokeSetMethod(t, field.getName(), 0f);
            } else if (field.getType() == Double.class) {
                invokeSetMethod(t, field.getName(), 0f);
            } else if (field.getType() == String.class) {
                invokeSetMethod(t, field.getName(), "");
            } else if (field.getType() == BigDecimal.class) {
                invokeSetMethod(t, field.getName(), BigDecimal.ZERO);
            } else if (field.getType() == BigInteger.class) {
                invokeSetMethod(t, field.getName(), BigInteger.ZERO);
            } else {
                LOG.info("初始化默认值类型不匹配,{},{}", t, field.getType());
            }
        }
    }

    public static String getCurrentMethodName() {
        try {
            return Thread.currentThread().getStackTrace()[2].getMethodName();
        } catch (Exception ex) {
            LOG.error("reflect invoke failure", ex);
            return new StringGenerator.UUIDGenerator().next();
        }
    }

    public static String getCurrentWholeMethodName() {
        try {
            String className = Thread.currentThread().getStackTrace()[2].getClassName();
            String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
            return String.format("%s.%s", className, methodName);
        } catch (Exception ex) {
            LOG.error("reflect invoke failure", ex);
            return new StringGenerator.UUIDGenerator().next();
        }
    }

    public static String getCurrentClassName() {
        try {
            return Thread.currentThread().getStackTrace()[2].getClassName();
        } catch (Exception ex) {
            LOG.error("reflect invoke failure", ex);
            return new StringGenerator.UUIDGenerator().next();
        }
    }

}
