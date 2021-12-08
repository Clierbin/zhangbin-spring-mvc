package com.zhangbin.spring.mvcframework.aop.intercept;


import com.zhangbin.spring.mvcframework.aop.aspect.BNMethodInterceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BNMethodInvocation implements BNJoinPoint{

    private  Method method;
    private  Object[] args=new Object[0];

    private Object proxyInstance;

    private  Object instance;
    private  Class instanceClass;
    private  List<Object> activeObject;

    private Map<String,Object> userAttributes = new HashMap<String, Object>();
    private  int activeIndex = -1;

    public BNMethodInvocation(Method method, Object[] args, Object proxyInstance, Object instance, Class instanceClass, List<Object> activeObject) {
        this.method = method;
        this.args = args;
        this.proxyInstance = proxyInstance;
        this.instance = instance;
        this.instanceClass = instanceClass;
        this.activeObject = activeObject==null?new ArrayList<>():activeObject;
    }

    public  Object press() throws Throwable {
        // 如果activeObject为空,或者切面方法已经全部调用完则执行 真正的方法
        if (activeIndex == activeObject.size() - 1) {
            return method.invoke(instance, args);
        }
        // 从代理方法里边取方法来执行
        Object o = activeObject.get(++activeIndex);
        // 如果是切面方法 则运行
        if (o instanceof BNMethodInterceptor){
            return ((BNMethodInterceptor) o).invoke(this);
        }else{
            // 递归调用 通过activeIndex不断递增 满足条件退出递归
            return press();
        }
    }

    @Override
    public Object getThis() {
        return this.instance;
    }

    @Override
    public Object[] getArguments() {
        return this.args;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        this.userAttributes.put(key,value);
    }

    @Override
    public Object getUserAttribute(String key) {
        return this.userAttributes.get(key);
    }
}
