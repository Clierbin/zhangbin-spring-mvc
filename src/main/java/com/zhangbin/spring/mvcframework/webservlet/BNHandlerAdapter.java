package com.zhangbin.spring.mvcframework.webservlet;

import com.zhangbin.spring.mvcframework.annotition.BNRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BNHandlerAdapter {
    public BNModleAndView handler(HttpServletRequest req, HttpServletResponse resp, BNHandlerMapping handler) throws Exception {

        // 方法的入参都可以取到
        Method method = handler.getMethod();
        Map<String, Integer> parmIndex = new HashMap<>();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        // 不特殊设置的话 只能解析带有注解的以及 HttpServletRequest HttpServletResponse
        // 这只能把写到了自定义注解的形参找出来
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            for (Annotation annotation : parameterAnnotation) {
                if (annotation instanceof BNRequestParam) {
                    String value = ((BNRequestParam) annotation).value();
                    parmIndex.put(value, i);
                }
            }
        }

        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (parameterType == HttpServletRequest.class || parameterType == HttpServletResponse.class) {
                parmIndex.put(parameterType.getSimpleName(), i);
            }
        }
        Object[] methodParm = new Object[parameterTypes.length];
        // 执行  从method 方法中找形参, 从reques 里边找入参
        Map<String, String[]> parameterMap = req.getParameterMap();
        for (String reqParm : parameterMap.keySet()) {
            String[] strings = parameterMap.get(reqParm);
            String parmVale = Arrays.toString(strings)
                    .replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", "");
            if (!parmIndex.containsKey(reqParm)) {
                continue;
            }
            Integer integer = parmIndex.get(reqParm);
            //涉及到类型强制转换
            methodParm[integer] = caseStringValue(parmVale, parameterTypes[integer]);
        }

        if (parmIndex.containsKey(HttpServletRequest.class.getSimpleName())) {
            Integer integer = parmIndex.get(HttpServletRequest.class.getSimpleName());
            methodParm[integer] = req;
        }
        if (parmIndex.containsKey(HttpServletResponse.class.getSimpleName())) {
            Integer integer = parmIndex.get(HttpServletResponse.class.getSimpleName());
            methodParm[integer] = resp;
        }
        // 执行方法调用
        Object invoke = method.invoke(handler.getController(), methodParm);
        if (Objects.isNull(invoke) || invoke instanceof Void) {
            return null;
        }
        // 判断是否是modleAndView 对象
        boolean isModleAndView = handler.getMethod().getReturnType() == BNModleAndView.class;
        if (isModleAndView) {
            return (BNModleAndView) invoke;
        }
        return null;
    }

    private Object caseStringValue(String parmvalue, Class<?> parameterType) {
        if (String.class == parameterType) {
            return parmvalue;
        } else if (Integer.class == parameterType) {
            return Integer.valueOf(parmvalue);
        } else {
            if (parmvalue != null) {
                return parmvalue;
            } else {
                return null;
            }
        }
    }
}
