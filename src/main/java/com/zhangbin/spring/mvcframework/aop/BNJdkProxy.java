package com.zhangbin.spring.mvcframework.aop;

import com.zhangbin.spring.mvcframework.aop.intercept.BNMethodInvocation;
import com.zhangbin.spring.mvcframework.aop.support.BNAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class BNJdkProxy implements BNAopProxy, InvocationHandler {
    private BNAdvisedSupport BNAdvisedSupport;

    public BNJdkProxy(BNAdvisedSupport BNAdvisedSupport) {
        this.BNAdvisedSupport = BNAdvisedSupport;
    }

    @Override
    public Object getProxy() {
       return getProxy(this.getClass().getClassLoader());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> chain= BNAdvisedSupport.getInterceptorsAndDynamicInterceptionAdvice(method,this.BNAdvisedSupport.getTargetClass());
        BNMethodInvocation methodIntece = new BNMethodInvocation(method,args,proxy,this.BNAdvisedSupport.getTarget(),
        this.BNAdvisedSupport.getTargetClass(),chain);
        return methodIntece.press();
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, BNAdvisedSupport.getTargetClass().getInterfaces(),this);
    }
}
