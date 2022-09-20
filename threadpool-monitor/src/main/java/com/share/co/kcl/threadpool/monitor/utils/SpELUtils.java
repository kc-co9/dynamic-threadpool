package com.share.co.kcl.threadpool.monitor.utils;

import com.alibaba.fastjson2.JSON;
import com.share.co.kcl.common.utils.ReflectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Objects;

public class SpELUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SpELUtils.class);

    /**
     * 将方法的参数名和参数值绑定
     *
     * @param params 方法的参数名
     * @param args   方法的参数值
     */
    public static EvaluationContext createContext(String[] params, Object[] args) {
        // 将参数名与参数值对应起来
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < params.length; i++) {
            context.setVariable(params[i], args[i]);
        }
        return context;
    }


    public static String parseSpELIgnoreError(String expression, EvaluationContext context) {
        try {
            return parseSpEL(expression, context);
        } catch (Exception ex) {
            LOG.warn("parseSpELIgnoreError method invokes failure", ex);
            return expression;
        }
    }

    public static String parseSpEL(String expression, EvaluationContext context) {
        if (StringUtils.isBlank(expression)) {
            return "";
        }
        ExpressionParser expressionParser = new SpelExpressionParser();
        Object value = expressionParser.parseExpression(expression).getValue(context);
        if (Objects.isNull(value)) {
            return "";
        }
        if (ReflectUtils.isPrimitive(value.getClass())) {
            return String.valueOf(value);
        }
        return JSON.toJSONString(value);
    }
}
