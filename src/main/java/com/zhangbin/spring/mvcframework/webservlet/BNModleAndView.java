package com.zhangbin.spring.mvcframework.webservlet;

import java.util.Map;

public class BNModleAndView {
    private String ViewName;

    private Map<String,?> modle;

    public BNModleAndView() {
    }

    public BNModleAndView(String viewName, Map<String, ?> modle) {
        ViewName = viewName;
        this.modle = modle;
    }

    public BNModleAndView(String viewName) {
        ViewName = viewName;
    }

    public String getViewName() {
        return ViewName;
    }

    public void setViewName(String viewName) {
        ViewName = viewName;
    }

    public Map<String, ?> getModle() {
        return modle;
    }

    public void setModle(Map<String, ?> modle) {
        this.modle = modle;
    }
}
