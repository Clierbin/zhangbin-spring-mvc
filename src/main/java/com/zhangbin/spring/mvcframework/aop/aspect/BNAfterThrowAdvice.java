package com.zhangbin.spring.mvcframework.aop.aspect;


import com.zhangbin.spring.mvcframework.aop.intercept.BNMethodInvocation;

import java.lang.reflect.Method;

public class BNAfterThrowAdvice extends BNBaseAdvice implements BNMethodInterceptor {
    private String throwName;

    public BNAfterThrowAdvice(Object aspectClass, Method aspectMethod) {
        super(aspectClass, aspectMethod);
    }

    @Override
    public Object invoke(BNMethodInvocation mi) throws Throwable {
        try {
             return mi.press();
        } catch (Throwable ex) {
            invokeAdviceMethod(mi, null, ex);
            throw ex;
        }
    }

    public void setThrowName(String throwName) {
        this.throwName = throwName;
    }
}
