package com.zhangbin.spring.mvcframework.aop.intercept;

import java.lang.reflect.Method;

/**
 * Created by Tom.
 */
public interface BNJoinPoint {

    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key,Object value);

    Object getUserAttribute(String key);

}
