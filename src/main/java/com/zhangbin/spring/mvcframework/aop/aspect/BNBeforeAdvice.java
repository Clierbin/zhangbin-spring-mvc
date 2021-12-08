package com.zhangbin.spring.mvcframework.aop.aspect;

import com.zhangbin.spring.mvcframework.aop.intercept.BNJoinPoint;
import com.zhangbin.spring.mvcframework.aop.intercept.BNMethodInvocation;

import java.lang.reflect.Method;

public class BNBeforeAdvice extends BNBaseAdvice implements BNMethodInterceptor {
    private BNJoinPoint jp;
    public BNBeforeAdvice(Object aspectClass, Method aspectMethod) {
        super(aspectClass, aspectMethod);
    }

    @Override
    public Object invoke(BNMethodInvocation mi) throws Throwable {
        jp = mi;
        before(adviceMethod,  mi.getArguments(),this);
        return mi.press();
    }

    public void before(Method method, Object[] arguments, Object aThis) throws Throwable {
        invokeAdviceMethod(this.jp,null,null);
    }
}
