package com.zhangbin.spring.mvcframework.aop.aspect;

import com.zhangbin.spring.mvcframework.aop.intercept.BNJoinPoint;

import java.lang.reflect.Method;

public abstract class BNBaseAdvice {
    protected Object aspect;
    protected Method adviceMethod;

    public BNBaseAdvice(Object aspectClass, Method aspectMethod) {
        this.aspect = aspectClass;
        this.adviceMethod = aspectMethod;
    }

    public void dodis(String methodName){
        System.out.println("AOP方法------->"+methodName);
    }

    protected Object invokeAdviceMethod(
            BNJoinPoint joinPoint, Object returnValue, Throwable ex)
            throws Throwable {
        Class<?> [] paramTypes = this.adviceMethod.getParameterTypes();
        if(null == paramTypes || paramTypes.length == 0){
            return this.adviceMethod.invoke(aspect);
        }else {
            Object[] args = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                if (paramTypes[i] == BNJoinPoint.class) {
                    args[i] = joinPoint;
                } else if (paramTypes[i] == Throwable.class) {
                    args[i] = ex;
                } else if (paramTypes[i] == Object.class) {
                    args[i] = returnValue;
                }
            }
            return this.adviceMethod.invoke(aspect, args);
        }
    }
}
