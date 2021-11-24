package com.zhangbin.spring.mvcframework.core;

public interface BNBeanFactory {
    Object getBean(String beanName);
    Object getBean(Class<?> beanName);
}
