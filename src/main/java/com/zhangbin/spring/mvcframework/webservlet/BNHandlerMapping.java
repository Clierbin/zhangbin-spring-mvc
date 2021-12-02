package com.zhangbin.spring.mvcframework.webservlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class BNHandlerMapping {

    private Object controller;
    protected Method method;
    protected Pattern pattern;

    public BNHandlerMapping(Pattern pattern, Object controller, Method method) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public Method getMethod() {
        return method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Object getController() {
        return controller;
    }

}
