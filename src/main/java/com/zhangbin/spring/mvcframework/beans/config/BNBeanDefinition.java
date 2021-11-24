package com.zhangbin.spring.mvcframework.beans.config;

public class BNBeanDefinition {
    public boolean isLazyInit(){return false;}
    private String beanName;
    private String beanClassName;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }
}
