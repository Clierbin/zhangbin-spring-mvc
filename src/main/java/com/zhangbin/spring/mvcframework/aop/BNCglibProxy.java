package com.zhangbin.spring.mvcframework.aop;

import com.zhangbin.spring.mvcframework.aop.support.BNAdvisedSupport;

public class BNCglibProxy implements BNAopProxy{

    private BNAdvisedSupport bnAdvisedSupport;

    public BNCglibProxy(BNAdvisedSupport bnAdvisedSupport) {
        this.bnAdvisedSupport = bnAdvisedSupport;
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
