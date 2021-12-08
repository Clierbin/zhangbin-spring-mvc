package com.zhangbin.spring.mvcframework.aop.aspect;

import com.zhangbin.spring.mvcframework.aop.intercept.BNJoinPoint;
import com.zhangbin.spring.mvcframework.aop.intercept.BNMethodInvocation;

import java.lang.reflect.Method;

public class BNAfterAdvice extends BNBaseAdvice implements BNMethodInterceptor {
    private BNJoinPoint jp;

    public BNAfterAdvice(Object aspectClass, Method aspectMethod) {
        super(aspectClass, aspectMethod);
    }

    @Override
    public Object invoke(BNMethodInvocation mi) throws Throwable {
        jp = mi;
        Object retVal = mi.press();
        this.after(retVal, mi.getMethod(), mi.getArguments(), mi.getThis());
        return retVal;
    }

    private void after(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        this.invokeAdviceMethod(jp,retVal,null);
    }

}

