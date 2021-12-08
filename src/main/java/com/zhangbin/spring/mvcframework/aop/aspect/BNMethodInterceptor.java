package com.zhangbin.spring.mvcframework.aop.aspect;

import com.zhangbin.spring.mvcframework.aop.intercept.BNMethodInvocation;

public interface BNMethodInterceptor {
    Object invoke(BNMethodInvocation invocation) throws Throwable;
}
