package com.zhangbin.spring.mvcframework.aop;

import com.zhangbin.spring.mvcframework.aop.support.BNAdvisedSupport;

public class BNDefaultAopProxyFactory {

    public static BNAopProxy getProxy(BNAdvisedSupport BNAdvisedSupport){
        if (BNAdvisedSupport.getTargetClass().getInterfaces().length>0) {
            return new BNJdkProxy(BNAdvisedSupport);
        }else{
            return  new BNCglibProxy(BNAdvisedSupport);
        }
    }
}
