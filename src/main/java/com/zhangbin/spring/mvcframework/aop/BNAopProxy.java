package com.zhangbin.spring.mvcframework.aop;

public interface BNAopProxy {

    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
